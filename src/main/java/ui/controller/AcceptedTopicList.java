package ui.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import model.TopicInfo;

/**
 * Controller class for the List of Proposed Topics. Can be instantiated by
 * calling its constructor and populated with a list of topics later.
 */
public class AcceptedTopicList {

	private AnchorPane root;

	private Map<String, AcceptedTopicListEntry> topics;

	@FXML
	private VBox topicList;

	public AnchorPane getRoot() {
		return this.root;
	}

	/**
	 * Constructor of the list. Loads the fxml for the list.
	 */
	public AcceptedTopicList() {
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/accepted_topic_list.fxml"));
		loader.setController(this);

		topics = new HashMap<String, AcceptedTopicListEntry>();

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
	 * Add a new accepted topic to the list.
	 * 
	 * @param topic The {@link TopicInfo} for the topic to add.
	 */
	protected void addTopic(TopicInfo topic) {
		AcceptedTopicListEntry entry = new AcceptedTopicListEntry(topic);
		topics.put(topic.getResourceUrl(), entry);
		topicList.getChildren().add(entry.getRoot());
	}

	/**
	 * Removes a given topic from the list of proposals.
	 * 
	 * @param resourceUrl The url of the resource, whose entry is to be removed.
	 */
	protected void removeTopicEntry(String resourceUrl) {
		AcceptedTopicListEntry entry = topics.remove(resourceUrl);
		topicList.getChildren().remove(entry.getRoot());
		topics.remove(resourceUrl);
	}
	

	public TopicInfo getTopic(String resourceUrl) {
		return topics.get(resourceUrl).getTopicInfo();
	}

}
