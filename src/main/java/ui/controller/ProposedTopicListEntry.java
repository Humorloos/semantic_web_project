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
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import model.TopicInfo;

/**
 * Controller class for an entry in the list of proposed topics. Contains
 * details such as the link to Wikipedia and a button to add the element to the
 * topics, that the user accepted.
 */
public class ProposedTopicListEntry {

	private Parent root;

	@FXML
	private Label resourceLabel, typeLabel, relationLabel, numberLabel;
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
		addTooltipToLabel(typeLabel);
		String previousTopicLabel = SWTApplication.getMainController()
				.getProposedTopicInfo(topicInfo.getPreviousResource()).getLabel();
		relationLabel.setText(topicInfo.getPropertyLabel() + ": " + previousTopicLabel);
		addTooltipToLabel(relationLabel);
		resourceLabel.setText(topicInfo.getLabel());
		addTooltipToLabel(resourceLabel);
		numberLabel.setText(topicInfo.getnRelatedPreviousResources() + "");
		addTooltipToLabel(numberLabel, "Number of connections to your topics");
		hyper1.setText("Wikipedia.com");

		root.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				try {
					SWTApplication.getTopicManager().addResourceToTopics(topicInfo.getResourceUrl());
					SWTApplication.getMainController().addTopicToAcceptedTopics(topicInfo);
					List<TopicInfo> result = SWTApplication.getTopicManager()
							.getSuggestionsForPreviousResources(SWTApplication.getNumberOfSuggestions());
					SWTApplication.getMainController().getproposedTopicList().clearAndPopulateList(result);
				} catch (InvalidUriInputException e1) {
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

	/**
	 * Adds the text of the given label as a tootltip to the label, so that the user
	 * can read it if the text does not fit in the entry.
	 * 
	 * @param label The label to add a {@link Tooltip} to.
	 * @param text  The text to add as a Tooltip.
	 */
	private void addTooltipToLabel(Label label, String text) {
		Tooltip t = new Tooltip(text);
		t.setShowDelay(Duration.millis(250));
		label.setTooltip(t);
	}

	/**
	 * Adds the text of the given label as a tootltip to the label, so that the user
	 * can read it if the text does not fit in the entry.
	 * 
	 * @param label The label to add a {@link Tooltip} to.
	 */
	private void addTooltipToLabel(Label label) {
		addTooltipToLabel(label, label.getText());
	}

	public Parent getRoot() {
		return root;
	}
}
