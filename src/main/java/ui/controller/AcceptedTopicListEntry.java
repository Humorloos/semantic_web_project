package ui.controller;


import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import application.SWTApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import model.TopicInfo;
import ui.util.TooltipHelper;

/**
 * Controller class for an entry in the list of proposed topics. Contains
 * details such as the link to Wikipedia and a button to add the element to the
 * topics, that the user accepted.
 */
public class AcceptedTopicListEntry {

	private Parent root;

	@FXML
	private Label resourceLabel;
	@FXML
	private Hyperlink link;

	private TopicInfo topic;
	
	@FXML
	private Button button1;

	public TopicInfo getTopicInfo() {
		return topic;
	}
	/**
	 * Constructor of the controller.
	 * 
	 * @param resourceUrl The URL, specifying the location of the resource, as a
	 *                    {@link String}.
	 * @param label       The label of the resource in english as a {@link String}.
	 * @param wikiUrl     Url to the Wikipedia article of the given resource as
	 *                    string.
	 */
	public AcceptedTopicListEntry(TopicInfo topic) {
		this.topic = topic;

		// load the fxml file for the entry
		FXMLLoader loader = new FXMLLoader(
				getClass().getClassLoader().getResource("fxml/accepted_topic_list_entry.fxml"));
		loader.setController(this);
		try {
			this.root = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		link.setText("wikipedia.com");
		TooltipHelper.addTooltipToLabel(link, "Read more on Wikipedia");
		resourceLabel.setText(topic.getLabel());	
		
		TooltipHelper.addTooltipToLabel(button1, "Remove from your topics");
		button1.setOnAction(e -> {
			SWTApplication.getTopicManager().removeResourceFromTopics(topic.getResourceUrl());
			SWTApplication.getMainController().removeTopicAcceptedTopics(topic);
		});
		
		link.setOnAction((ActionEvent e) -> {
			try {
				Desktop.getDesktop().browse(new URI(topic.getWikiUri()));
			} catch (IOException | URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
	}

	public Parent getRoot() {
		return root;
	}	
}
