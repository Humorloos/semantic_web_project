package backend;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.core.ResultBinding;

import com.google.common.collect.Sets;

import backend.exception.InvalidUriInputException;
import model.TopicInfo;

public class TopicManagerImpl implements TopicManager {

  public static final String RESOURCE_URI = "http://dbpedia.org/resource/";
  private static final String ONTOLOGY_URI = "http://dbpedia.org/ontology/";
  private static final String DBPEDIA_SPARQL_ENDPOINT = "http://dbpedia.org/sparql";
  private static final String SPARQL_PREFIXES = "PREFIX dbr: <" + RESOURCE_URI + "> "
      + "PREFIX dbo: <" + ONTOLOGY_URI + "> "
      + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ";
  private static final String MEANINGLESS_PROPERTY = "dbo:wikiPageWikiLink";


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
            + "CONSTRUCT { <" + resourceUrl + "> ?p ?o }"
            + "WHERE { <" + resourceUrl + "> ?p ?o "
            + "FILTER ("
            + "  ?p != " + MEANINGLESS_PROPERTY 
            + "  && "
            + "      strstarts(str(?o), \"" + RESOURCE_URI + "\")"
            + ")}"
            );
    final Query constructObjectQuery = QueryFactory.create(SPARQL_PREFIXES
            + "CONSTRUCT { ?s ?p <" + resourceUrl + "> } "
            + "WHERE { ?s ?p <" + resourceUrl + ">"
            + "FILTER ("
            + "  ?p != " + MEANINGLESS_PROPERTY 
            + "    &&"
            + "      strstarts(str(?s), \"" + RESOURCE_URI + "\")"
            + ")}"
            );
    final QueryExecution constructSubjectExecution = QueryExecutionFactory.sparqlService(DBPEDIA_SPARQL_ENDPOINT, constructSubjectQuery);
    final QueryExecution constructObjectExecution = QueryExecutionFactory.sparqlService(DBPEDIA_SPARQL_ENDPOINT, constructObjectQuery);
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
    // loop for paging: run the query with 'pageSize' results until enough resources are found
    while (resourcesFound < numOfSuggestions) {
    	// order resources in memoryModel by number of connections to previous resources
        String query = SPARQL_PREFIXES +
                "SELECT ?new_word (GROUP_CONCAT(DISTINCT ?old_word) AS ?old_words) "
                + "WHERE { { ?old_word ?p ?new_word " + previousResourceFilter + "} "
                + "UNION {?new_word ?p ?old_word " + previousResourceFilter + "}} "
                + "GROUP BY ?new_word "
                + "ORDER BY DESC(COUNT(DISTINCT ?old_word))"
                + "LIMIT " + pageSize + " OFFSET " + queryCount*pageSize;
        final ResultSet orderedResources = getSelectQueryResultSet(query);
        queryCount++;
        // stop queries if the result is empty
        if(!orderedResources.hasNext()) break; 
        // Get highest ranking numberOfProposals resources that are connected to the current resource
        while(orderedResources.hasNext() && resourcesFound < numOfSuggestions) {
        	final QuerySolution nextProposal = orderedResources.next();
        	final Set<String> old_words = Sets.newHashSet(nextProposal.get("?old_words").toString().split(" "));
        	if (old_words.contains(currentTopic)) {
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
 // Add labels of types to memoryModel
    final Query typeQuery = QueryFactory.create(SPARQL_PREFIXES
        + "CONSTRUCT { ?s rdfs:type ?tl } "
        + "WHERE { ?s dbo:type ?type. "
        + "?type rdfs:label ?tl. "
        + "FILTER ("
        + "  ?s IN (<" + String.join(">, <", proposals) + ">)"
        + "  && langMatches(lang(?tl), \"EN\")"
        + ")}");
    final QueryExecution typeExecution = QueryExecutionFactory.sparqlService(DBPEDIA_SPARQL_ENDPOINT,
    		typeQuery);
    memoryModel = typeExecution.execConstruct(memoryModel);
    memoryModel.write(System.out, "TURTLE");
    // Get a random property that connects each new resource to the current resource together with the new resources'
    // uris and labels
    final ResultSet result = getSelectQueryResultSet(SPARQL_PREFIXES +
        "SELECT ?uri ?label ?type_label (SAMPLE(?property) AS ?sample_property)"
        + "WHERE { { ?current_word ?property ?uri} "
        + "UNION {?uri ?property ?current_word} "
        + "UNION { ?current_word rdfs:type ?type_label } "
        + "?uri rdfs:label ?label "
        + "FILTER(?property != " + MEANINGLESS_PROPERTY
        + " && ?current_word = <" + currentTopic + ">"
        + " && ?uri IN (<" + String.join(">, <", proposals) + ">))} "
        + "GROUP BY ?uri ?label");
    return IteratorUtils.toList(result);
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

  }

  private ResultSet getSelectQueryResultSet(final String queryString) {
    final Query propertyQuery = QueryFactory.create(queryString);
    final QueryExecution propertyExecution = QueryExecutionFactory.create(propertyQuery, memoryModel);
    return propertyExecution.execSelect();
  }
}
