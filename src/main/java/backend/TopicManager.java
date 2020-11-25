package backend;

import backend.exception.InvalidUriInputException;
import java.util.List;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Resource;

import model.TopicInfo;

/**
 * Interface defining a class that manages the topics of a user.
 * 
 */
public interface TopicManager {

	/**
	 * Add the given resource to the user's list of topics.
	 *
	 * @param resourceUrl The Url identifying the resource to add.
	 * @return The label of the resource.
	 */
	String addResourceToTopics(String resourceUrl) throws InvalidUriInputException;

	/**
	 * Get specific information on a given resource (Link to wikipedia etc.)
	 * 
	 * @param resourceUrl The Url identifying the {@link Resource} to remove.
	 * @return {@link TopicInfo} of some sort, containing info about the topic.
	 */
	TopicInfo getInformationAboutTopic(String resourceUrl);

	/**
	 * Get a List of resources that would be suggested based on all accepted topics
	 * so far. If there already are suggestions stored, those will be returned,
	 * otherwise the {@link TopicManagerImpl} will fetch a new list of suggestions.
	 * 
	 * @param numOfSuggestions The number of suggestions to make.
	 * @return {@link List} of {@link QuerySolution}s with the topics that were
	 *         suggested.
	 */
	List<QuerySolution> getSuggestionsForAllTopics(int numOfSuggestions);

	/**
	 * Get a List of resources that would be suggested based on a given resource. If
	 * there already are suggestions stored, those will be returned, otherwise the
	 * {@link TopicManagerImpl} will fetch a new list of suggestions.
	 * 
	 * @param numOfSuggestions The number of suggestions to make.
	 * @return {@link List} of {@link QuerySolution}s with the topics that were
	 *         suggested.
	 */
	List<QuerySolution> getSuggestionsForCurrentTopic(int numOfSuggestions);

	/**
	 * Loads all previously suggested topics for a given initial resource. If there
	 * are none, initiate and return a new list for the given resource.
	 * 
	 * @param initialResource The Url identifying the initial {@link Resource} to
	 *                        use as a base for searching.
	 * @return A List of Strings with the topics that were suggested so far.
	 */
	List<String> loadAcceptedTopicsForInitialResource(String initialResource);

	/**
	 * Remove the given resource from the user's list of topics.
	 * 
	 * @param resourceUrl The Url identifying the {@link Resource} to remove.
	 */
	void removeResourceFromTopics(String resourceUrl);

//	/**
//	 * TODO Blacklist is an optional feature for now.
//	 */
//	public void addResourceToBlackList(String resourceUrl);

//	public void removeResourceFromBlackList(String resourceUrl);

}
