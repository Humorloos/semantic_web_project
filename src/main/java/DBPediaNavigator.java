import com.google.inject.internal.util.Sets;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.util.List;
import java.util.Set;

public class DBPediaNavigator {
    String RESOURCE_URI = "http://dbpedia.org/resource/";
    String ONTOLOGY_URI = "http://dbpedia.org/ontology/";
    String DBPEDIA_SPARQL_ENDPOINT = "http://dbpedia.org/sparql";
    String SPARQL_PREFIXES = "PREFIX dbr: <" + RESOURCE_URI + "> PREFIX dbo: <" + ONTOLOGY_URI + "> ";


    protected Model memoryModel;
    protected Set<String> previousResources;

    public DBPediaNavigator() {
        memoryModel = ModelFactory.createDefaultModel();
        previousResources = Sets.newHashSet();
    }

    public void registerNewResource(String newResource) {
        Query describeQuery = QueryFactory.create(SPARQL_PREFIXES + "DESCRIBE dbr:" + newResource);
        QueryExecution describeExecution = QueryExecutionFactory.sparqlService(DBPEDIA_SPARQL_ENDPOINT, describeQuery);
        Model description = describeExecution.execDescribe();
        Query constructQuery = QueryFactory.create(SPARQL_PREFIXES +
                "CONSTRUCT { ?s ?p ?o } " +
                "WHERE { ?s ?p ?o " +
                "FILTER (?p != dbo:wikiPageWikiLink " +
                "&& ((?s=dbr:" + newResource + " && regex(str(?o), \"" + RESOURCE_URI + "\")) " +
                "|| (?o=dbr:" + newResource + " && regex(str(?s), \"" + RESOURCE_URI + "\"))))}");
        QueryExecution constructExecution = QueryExecutionFactory.create(constructQuery, description);
        memoryModel = constructExecution.execConstruct(memoryModel);
        previousResources.add(newResource);
    }

    public List<QuerySolution> findNextDestinations() {
        String previousResourceFilter = "FILTER(?new_word Not IN (dbr:" + String.join(", dbr:", previousResources) + "))";
        Query orderQuery = QueryFactory.create(SPARQL_PREFIXES +
                "SELECT ?new_word (count(distinct ?old_word) AS ?occurrences) " +
                "WHERE { { ?old_word ?p ?new_word " + previousResourceFilter + "} " +
                "UNION {?new_word ?p ?old_word " + previousResourceFilter + "}}" +
                "GROUP BY ?new_word ORDER BY desc(?occurrences)");
        QueryExecution orderExecution = QueryExecutionFactory.create(orderQuery, memoryModel);
        ResultSet result = orderExecution.execSelect();
        return IteratorUtils.toList(result);
    }

}
