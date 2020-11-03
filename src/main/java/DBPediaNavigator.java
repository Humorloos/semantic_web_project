import com.google.inject.internal.util.Sets;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.util.List;
import java.util.Set;

public class DBPediaNavigator {
    private static final String RESOURCE_URI = "http://dbpedia.org/resource/";
    private static final String ONTOLOGY_URI = "http://dbpedia.org/ontology/";
    private static final String DBPEDIA_SPARQL_ENDPOINT = "http://dbpedia.org/sparql";
    private static final String SPARQL_PREFIXES =
        "PREFIX dbr: <" + RESOURCE_URI + "> PREFIX dbo: <" + ONTOLOGY_URI + "> ";

    /** Model containing all valid triples belonging to previously seen resources. */
    protected Model memoryModel = ModelFactory.createDefaultModel();
    /** Set of Resources that were already seen by the user. */
    protected Set<String> previousResources = Sets.newHashSet();

    /**
     * Loads all triples belonging to the provided resource from DBPedia and adds the valid ones to the 
     * memoryModel. Valid triples are those whose predicate is not dbo:wikiPageWikiLink (because the meaning of these
     * triples is rather limited) and whose subject and object both are from DBPedia.
     * @param newResource Name of the new resource to register
     */
    public void registerNewResource(final String newResource) {
        final Query describeQuery = QueryFactory.create(SPARQL_PREFIXES + "DESCRIBE dbr:" + newResource);
        final QueryExecution describeExecution = QueryExecutionFactory.sparqlService(DBPEDIA_SPARQL_ENDPOINT,
            describeQuery);
        final Model description = describeExecution.execDescribe();
        final Query constructQuery = QueryFactory.create(SPARQL_PREFIXES +
                "CONSTRUCT { ?s ?p ?o } " +
                "WHERE { ?s ?p ?o " +
                "FILTER (?p != dbo:wikiPageWikiLink " +
                "&& ((?s=dbr:" + newResource + " && regex(str(?o), \"" + RESOURCE_URI + "\")) " +
                "|| (?o=dbr:" + newResource + " && regex(str(?s), \"" + RESOURCE_URI + "\"))))}");
        final QueryExecution constructExecution = QueryExecutionFactory.create(constructQuery, description);
        memoryModel = constructExecution.execConstruct(memoryModel);
        previousResources.add(newResource);
    }

    /**
     * Gets the resources from the memory graph ordered by the number of previously seen resources they are connected
     * to.
     * @return List of QuerySolutions containing the names of the resources (?new_word) and the number of previously
     * seen resources the resources are connected to (?occurrences)
     */
    public List<QuerySolution> findNextDestinations() {
        final String previousResourceFilter =
            "FILTER(?new_word Not IN (dbr:" + String.join(", dbr:", previousResources) + "))";
        final Query orderQuery = QueryFactory.create(SPARQL_PREFIXES +
                "SELECT ?new_word (count(distinct ?old_word) AS ?occurrences) " +
                "WHERE { { ?old_word ?p ?new_word " + previousResourceFilter + "} " +
                "UNION {?new_word ?p ?old_word " + previousResourceFilter + "}}" +
                "GROUP BY ?new_word ORDER BY desc(?occurrences)");
        final QueryExecution orderExecution = QueryExecutionFactory.create(orderQuery, memoryModel);
        final ResultSet result = orderExecution.execSelect();
        return IteratorUtils.toList(result);
    }

}
