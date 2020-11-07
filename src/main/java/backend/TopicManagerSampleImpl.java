package backend;

import java.util.List;

import org.apache.jena.query.QuerySolution;

import model.TopicInfo;

/**
 * Sample implementation of the {@link TopicManager} that provides the ui with
 * some data, until the interface is properly implemented.
 */
public class TopicManagerSampleImpl implements TopicManager {

	private DBPediaNavigator navigator;

	public TopicManagerSampleImpl() {
		navigator = new DBPediaNavigator();
	}

	@Override
	public void addResourceToTopics(String resourceUrl) {
		navigator.registerNewResource(resourceUrl);
	}

	@Override
	public TopicInfo getInformationAboutTopic(String resourceUrl) {
		return new TopicInfo("http://dbpedia.org/page/Mannheim", "Hello! I am a placeholder",
				"http://en.wikipedia.org/wiki/Mannheim");
	}

	@Override
	public List<QuerySolution> getSuggestionsForAllTopics(int numOfSuggestions) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<QuerySolution> getSuggestionsForCurrentTopic(int numOfSuggestions) {
		return navigator.findNextProposals(navigator.getPreviousResources().iterator().next());
	}

	@Override
	public List<String> loadAcceptedTopicsForInitialResource(String initialResource) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeResourceFromTopics(String resourceUrl) {
		// TODO Auto-generated method stub

	}

}
