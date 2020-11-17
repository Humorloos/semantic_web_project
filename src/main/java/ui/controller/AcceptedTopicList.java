package ui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

	private List<AcceptedTopicListEntry> topics;

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

		topics = new ArrayList<AcceptedTopicListEntry>();

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
		topics.add(entry);
		topicList.getChildren().add(entry.getRoot());
	}

	/**
	 * Removes a given topic from the list of proposals.
	 * 
	 * @param topicEntry The {@link AcceptedTopicListEntry} to remove.
	 */
	protected void removeTopicEntry(AcceptedTopicListEntry topicEntry) {
		topics.remove(topicEntry);
		topicList.getChildren().remove(topicEntry.getRoot());
	}

}
