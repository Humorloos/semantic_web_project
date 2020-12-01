package ui.controller;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import application.SWTApplication;
import backend.exception.InvalidUriInputException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import model.TopicInfo;

/**
 * Controller class for an entry in the list of proposed topics. Contains
 * details such as the link to Wikipedia and a button to add the element to the
 * topics, that the user accepted.
 */
public class ProposedTopicListEntry {

	private Parent root;

	@FXML
	private Label resourceLabel, typeLabel, relationLabel;
	@FXML
	private Hyperlink hyper1;

	private TopicInfo topicInfo;

	/**
	 * Constructor of the controller.
	 */
	public ProposedTopicListEntry(TopicInfo info) {
		/*
		 * Store attributes in the controller for later use.
		 */
		this.topicInfo = info;

		// load the fxml file for the entry
		FXMLLoader loader = new FXMLLoader(
				getClass().getClassLoader().getResource("fxml/proposed_topic_list_entry.fxml"));
		loader.setController(this);
		try {
			this.root = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		typeLabel.setText(topicInfo.getType());
		String previousTopicLabel = SWTApplication.getMainController()
				.getProposedTopicInfo(topicInfo.getPreviousResource()).getLabel();
		relationLabel.setText(topicInfo.getPropertyLabel() + ": " + previousTopicLabel);
		resourceLabel.setText(topicInfo.getLabel());
		hyper1.setText("Wikipedia.com");
		// btn1.setText("add to my topics ");
		// btn1.setOnAction(e -> {
		// try {
		// SWTApplication.getTopicManager().addResourceToTopics(topicInfo.getResourceUrl());
		// SWTApplication.getMainController().addTopicToAcceptedTopics(topicInfo);
		// SWTApplication.getTopicManager().getSuggestionsForPreviousResources(SWTApplication.getNumberOfSuggestions());
		// } catch (InvalidUriInputException e1) {
		// // TODO add Alert?
		// e1.printStackTrace();
		// }
		// }); Button removed

		root.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				try {
				SWTApplication.getTopicManager().addResourceToTopics(topicInfo.getResourceUrl());
				SWTApplication.getMainController().addTopicToAcceptedTopics(topicInfo);
				List<TopicInfo> result = SWTApplication.getTopicManager().getSuggestionsForPreviousResources(
						SWTApplication.getNumberOfSuggestions());
				SWTApplication.getMainController().getproposedTopicList().clearAndPopulateList(result);
				}catch(InvalidUriInputException e1) {
					Alert a = new Alert(Alert.AlertType.ERROR, "No Common Resources");
					a.showAndWait();
				}
			}
		});

		hyper1.setOnAction((ActionEvent e) -> {
			try {
				Desktop.getDesktop().browse(new URI(info.getWikiUri()));
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
