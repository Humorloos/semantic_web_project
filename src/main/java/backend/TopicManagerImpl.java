package backend;


import backend.exception.InvalidUriInputException;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import model.TopicInfo;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.sparql.core.ResultBinding;

public class TopicManagerImpl implements TopicManager {

  public static final String RESOURCE_URI = "http://dbpedia.org/resource/";
  private static final String ONTOLOGY_URI = "http://dbpedia.org/ontology/";
  private static final String DBPEDIA_SPARQL_ENDPOINT = "http://dbpedia.org/sparql";
  private static final String SPARQL_PREFIXES = "PREFIX dbr: <" + RESOURCE_URI + "> "
      + "PREFIX dbo: <" + ONTOLOGY_URI + "> "
      + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
      + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>";
  private static final Set<String> MEANINGLESS_PROPERTIES = Set.of("dbo:wikiPageWikiLink", "rdf:type", "rdfs:label");

  /**
   * Model containing all valid triples belonging to previously seen resources.
   */
  protected Model memoryModel = ModelFactory.createDefaultModel();
  /**
   * Set of Resources that were already seen by the user.
   */
  protected Set<String> previousResources = Sets.newHashSet();
  /**
   * Resource that is currently being displayed to the user.
   */
  protected String currentTopic;

  /**
   * Loads all triples belonging to the provided resource from DBPedia and adds the valid ones to the memoryModel. Valid
   * triples are those whose predicate is not dbo:wikiPageWikiLink (because the meaning of these triples is rather
   * limited) and whose subject and object both are from DBPedia.
   *
   * @param resourceUrl URI of the resource to add
   * @throws InvalidUriInputException if the provided resource is not specified in DBPedia
   */
  @Override
  public void addResourceToTopics(final String resourceUrl) throws InvalidUriInputException {
    final Query constructSubjectQuery = QueryFactory.create(SPARQL_PREFIXES
        + "CONSTRUCT { <" + resourceUrl + "> ?p ?o . ?o rdf:type ?type }"
        + "WHERE { <" + resourceUrl + "> ?p ?o . "
        + "        ?o rdf:type ?type "
        + "FILTER ("
        + "  ?p NOT IN (" + String.join(",", MEANINGLESS_PROPERTIES) + ")"
        + "  && "
        + "      strstarts(str(?o), \"" + RESOURCE_URI + "\")"
        + ")}"
    );
    final Query constructObjectQuery = QueryFactory.create(SPARQL_PREFIXES
        + "CONSTRUCT { ?s ?p <" + resourceUrl + "> . ?s rdf:type ?type } "
        + "WHERE { ?s ?p <" + resourceUrl + "> . "
        + "        ?s rdf:type ?type "
        + "FILTER ("
        + "  ?p NOT IN (" + String.join(",", MEANINGLESS_PROPERTIES) + ")"
        + "  &&"
        + "    strstarts(str(?s), \"" + RESOURCE_URI + "\")"
        + ")}"
    );
    final QueryExecution constructSubjectExecution = QueryExecutionFactory
        .sparqlService(DBPEDIA_SPARQL_ENDPOINT, constructSubjectQuery);
    final QueryExecution constructObjectExecution = QueryExecutionFactory
        .sparqlService(DBPEDIA_SPARQL_ENDPOINT, constructObjectQuery);
    Model newModel = constructSubjectExecution.execConstruct();
    newModel = newModel.add(constructObjectExecution.execConstruct(newModel));
    if (newModel.isEmpty()) {
      throw new InvalidUriInputException(resourceUrl);
    }
    memoryModel = memoryModel.add(newModel);
    previousResources.add(resourceUrl);
    currentTopic = resourceUrl;
  }

  /**
   * Finds numOfSuggestions resources with most links to previous resources and at least one link to the current
   * resource, ordered by the number of links to previous resources together with one property each connecting them to
   * the current resource.
   *
   * @param numOfSuggestions the number of resources to return as suggestisons
   * @return List of {@link ResultBinding}, each containing a variable "uri" which is bound to the new resource's URI, a
   * variable "label" which is bound to the new resource's label, and a variable "sample_property" which is bound to one
   * property connecting the new resource to the current resource
   */
  @Override
  public List<QuerySolution> getSuggestionsForCurrentTopic(final int numOfSuggestions) {
    final String previousResourceFilter =
        "FILTER(?new_word NOT IN (<" + String.join(">, <", previousResources) + ">))";

    // parameters for paging
    final int pageSize = 50;
    int queryCount = 0, resourcesFound = 0;
    final Set<String> proposals = new HashSet<>();
    final Set<String> presentTypes = new HashSet<>();
    // loop for paging: run the query with 'pageSize' results until enough resources are found
    while (resourcesFound < numOfSuggestions) {
      // order resources in memoryModel by number of connections to previous resources
      String query = SPARQL_PREFIXES +
          "SELECT ?new_word (GROUP_CONCAT(DISTINCT ?type) AS ?types)"
          + "WHERE { { ?old_word ?p ?new_word " + previousResourceFilter + "} "
          + "UNION {?new_word ?p ?old_word " + previousResourceFilter + "} . "
          + "?new_word rdf:type ?type "
          + "FILTER (?p NOT IN (" + String.join(",", MEANINGLESS_PROPERTIES) + "))} "
          + "GROUP BY ?new_word "
          + "ORDER BY DESC(COUNT(DISTINCT ?old_word))"
          + "LIMIT " + pageSize + " OFFSET " + queryCount * pageSize;
      final ResultSet orderedResources = getSelectQueryResultSet(query);
      queryCount++;
      // stop queries if the result is empty
      if (!orderedResources.hasNext()) {
        break;
      }
      // Get highest ranking numberOfProposals resources that have at least one unique type
      while (orderedResources.hasNext() && resourcesFound < numOfSuggestions) {
        final QuerySolution nextProposal = orderedResources.next();
        // Only add a resource to the proposals if there it has at least one type that is not yet contained in the
        // types of already present proposals
        final Set<String> proposalTypeSet = Sets.newHashSet(nextProposal.get("?types").toString().split(" "));
        proposalTypeSet.removeAll(presentTypes);
        if (proposalTypeSet.size() > 0) {
          presentTypes.addAll(proposalTypeSet);
          resourcesFound++;
          proposals.add(nextProposal.get("?new_word").toString());
        }
      }
    }

    // Add labels of proposals to memoryModel
    final Query constructQuery = QueryFactory.create(SPARQL_PREFIXES
        + "CONSTRUCT { ?s ?p ?o } "
        + "WHERE { ?s ?p ?o "
        + "FILTER ("
        + "  ?s IN (<" + String.join(">, <", proposals) + ">)"
        + "  && ?p = rdfs:label"
        + "  && langMatches(lang(?o), \"EN\")"
        + ")}");
    final QueryExecution constructExecution = QueryExecutionFactory.sparqlService(DBPEDIA_SPARQL_ENDPOINT,
        constructQuery);
    memoryModel = constructExecution.execConstruct(memoryModel);
    // Get uris and labels of the proposals and a previous resource and the property that connects the proposals to
    // that resource.
    return proposals.stream().map(proposal -> getSelectQueryResultSet(SPARQL_PREFIXES +
        "SELECT ?uri ?label ?sample_property ?previous_topic "
        + "WHERE { { ?previous_topic ?sample_property ?uri } "
        + "UNION {?uri ?sample_property ?previous_topic} "
        + "?uri rdfs:label ?label "
        + "FILTER(?sample_property NOT IN (" + String.join(",", MEANINGLESS_PROPERTIES) + ")"
        + " && ?uri = <" + proposal + ">)} ").next()).collect(Collectors.toList());
  }

  @Override
  public TopicInfo getInformationAboutTopic(final String resourceUrl) {
    return null;
  }

  @Override
  public List<QuerySolution> getSuggestionsForAllTopics(final int numOfSuggestions) {
    return null;
  }

  @Override
  public List<String> loadAcceptedTopicsForInitialResource(final String initialResource) {
    return null;
  }

  @Override
  public void removeResourceFromTopics(final String resourceUrl) {
	  StmtIterator iter = memoryModel.listStatements();
	  while (iter.hasNext()) {
		  Statement stmt = iter.nextStatement();
		  Resource  subject = stmt.getSubject(); 
		  RDFNode   object    = stmt.getObject();
		  Property  predicate = stmt.getPredicate();
		  if(object.toString().equals(resourceUrl)||subject.toString().equals(resourceUrl) || predicate.toString().equals(resourceUrl)) {
			  Statement stmt_delete = memoryModel.createStatement
                      (memoryModel.createResource(subject),
                       memoryModel.createProperty(predicate.toString()),
                       memoryModel.createResource(object.toString())
                      );
              memoryModel.remove(stmt_delete);
              System.out.println("deleted the triples" + subject + " " + predicate + " " + object);
		  }
	  }
	  this.previousResources.remove(resourceUrl);
	  currentTopic = "";
  }

  private ResultSet getSelectQueryResultSet(final String queryString) {
    final Query propertyQuery = QueryFactory.create(queryString);
    final QueryExecution propertyExecution = QueryExecutionFactory.create(propertyQuery, memoryModel);
    return propertyExecution.execSelect();
  }
}
