package ui.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.jena.query.QuerySolution;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import model.TopicInfo;

/**
 * Controller class for the List of Proposed Topics. Can be instantiated by
 * calling its constructor and populated with a list of topics later.
 */
public class ProposedTopicList {

	private AnchorPane root;

	private HashMap<String, ProposedTopicListEntry> topics;

	@FXML
	private VBox topicList;

	public AnchorPane getRoot() {
		return this.root;
	}

	/**
	 * Constructor of the list. Loads the fxml for the list.
	 */
	public ProposedTopicList() {
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/proposed_topic_list.fxml"));
		loader.setController(this);

		topics = new HashMap<String, ProposedTopicListEntry>();

		try {
			this.root = loader.load();
			AnchorPane.setBottomAnchor(root, 0.0);
			AnchorPane.setRightAnchor(root, 0.0);
			AnchorPane.setTopAnchor(root, 0.0);
			AnchorPane.setLeftAnchor(root, 0.0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method clears the list if it has any content and populates it again with
	 * the given list of topics.
	 * 
	 * @param proposals {@link List} of {@link QuerySolution}s, containing
	 *                  information about each proposed topic.
	 */
	protected void clearAndPopulateList(List<QuerySolution> proposals) {
		topicList.getChildren().clear();

		for (QuerySolution resource : proposals) {
			// TODO currently generates a 'label' from the URL -> use actual label
			String url = resource.get("uri").toString();

			ProposedTopicListEntry entry = new ProposedTopicListEntry(
					new TopicInfo(url, resource.get("label").toString(), resource.get("type_label").toString()));
			topics.put(url, entry);
			topicList.getChildren().add(entry.getRoot());
		}

	}

	/**
	 * Removes a given topic from the list of proposals.
	 * 
	 * @param resourceUrl The url of the resource, whose entry is to be removed.
	 */
	protected void removeTopic(String resourceUrl) {
		ProposedTopicListEntry entry = topics.remove(resourceUrl);
		topicList.getChildren().remove(entry.getRoot());
	}

}
