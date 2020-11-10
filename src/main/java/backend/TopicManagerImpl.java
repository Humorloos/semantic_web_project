package backend;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.TopicInfo;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.core.ResultBinding;

import com.google.common.collect.Sets;

public class TopicManagerImpl implements TopicManager{

  public static final String RESOURCE_URI = "http://dbpedia.org/resource/";
  private static final String ONTOLOGY_URI = "http://dbpedia.org/ontology/";
  private static final String DBPEDIA_SPARQL_ENDPOINT = "http://dbpedia.org/sparql";
  private static final String SPARQL_PREFIXES =
      "PREFIX dbr: <" + RESOURCE_URI + "> PREFIX dbo: <" + ONTOLOGY_URI + "> ";
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
   */
  @Override
  public void addResourceToTopics(final String resourceUrl) {
    final Query describeQuery = QueryFactory.create(SPARQL_PREFIXES + "DESCRIBE <" + resourceUrl + ">");
    final QueryExecution describeExecution = QueryExecutionFactory.sparqlService(DBPEDIA_SPARQL_ENDPOINT,
        describeQuery);
    final Model description = describeExecution.execDescribe();
    final Query constructQuery = QueryFactory.create(SPARQL_PREFIXES +
        "CONSTRUCT { ?s ?p ?o } " +
        "WHERE { ?s ?p ?o " +
        "FILTER (?p != " + MEANINGLESS_PROPERTY + " " +
        "&& ((?s=<" + resourceUrl + "> && regex(str(?o), \"" + RESOURCE_URI + "\")) " +
        "|| (?o=<" + resourceUrl + "> && regex(str(?s), \"" + RESOURCE_URI + "\"))))}");
    final QueryExecution constructExecution = QueryExecutionFactory.create(constructQuery, description);
    memoryModel = constructExecution.execConstruct(memoryModel);
    previousResources.add(resourceUrl);
    currentTopic = resourceUrl;
  }

  /**
   * Finds numOfSuggestions resources with most links to previous resources and at least one link to the
   * current resource, ordered by the number of links to previous resources together with one property each connecting
   * them to the current resource.
   *
   * @param numOfSuggestions the number of resources to return as suggestisons
   * @return List of {@link ResultBinding}, each containing a variable "new_word" which is bound to the new resource and
   * a variable "sample_property" which is bound to one property connecting the new resource to the current resource
   */
  @Override
  public List<QuerySolution> getSuggestionsForCurrentTopic(final int numOfSuggestions) {
    final String previousResourceFilter =
        "FILTER(?new_word NOT IN (<" + String.join(">, <", previousResources) + ">))";
    // order resources in memoryModel by number of connections to previous resources
    final ResultSet orderedResources = getSelectQueryResultSet(SPARQL_PREFIXES +
        "SELECT ?new_word (GROUP_CONCAT(DISTINCT ?old_word) AS ?old_words) "
        + "WHERE { { ?old_word ?p ?new_word " + previousResourceFilter + "} "
        + "UNION {?new_word ?p ?old_word " + previousResourceFilter + "}} "
        + "GROUP BY ?new_word "
        + "ORDER BY DESC(COUNT(DISTINCT ?old_word))");
    // Get highest ranking numberOfProposals resources that are connected to the current resource
    final Set<String> proposals = new HashSet<>();
    for (int i = 0; i < numOfSuggestions; ) {
      final QuerySolution nextProposal = orderedResources.next();
      final Set<String> old_words = Sets.newHashSet(nextProposal.get("?old_words").toString().split(" "));
      if (old_words.contains(currentTopic)) {
        proposals.add(orderedResources.next().get("?new_word").toString());
        i++;
      }
    }
    // Get a random property that connects each new resource to the current resource
    final ResultSet result = getSelectQueryResultSet(SPARQL_PREFIXES +
        "SELECT ?new_word (SAMPLE(?property) AS ?sample_property) "
        + "WHERE { { ?current_word ?property ?new_word } "
        + "UNION {?new_word ?property ?current_word} "
        + "FILTER(?property != " + MEANINGLESS_PROPERTY
        + " && ?current_word = <" + currentTopic + ">"
        + " && ?new_word IN (<" + String.join(">, <", proposals) + ">))} "
        + "GROUP BY ?new_word");
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
