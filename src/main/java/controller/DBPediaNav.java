package controller;

import java.util.List;
import java.util.Set;

import org.apache.jena.query.QuerySolution;

public interface DBPediaNav {
	void registerNewResource(final String newResource);
	List<QuerySolution> findNextProposals(final String currentResource);
	//ResultSet getSelectQueryResultSet(final String queryString);
	Set<String> getPreviousResources();
	void setNumber(int num);
}
