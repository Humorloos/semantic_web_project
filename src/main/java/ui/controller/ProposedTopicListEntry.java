package ui.controller;


import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import application.SWTApplication;
import backend.exception.InvalidUriInputException;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import model.TopicInfo;

/**
 * Controller class for an entry in the list of proposed topics. Contains
 * details such as the link to Wikipedia and a button to add the element to the
 * topics, that the user accepted.
 */
public class ProposedTopicListEntry {

	private Parent root;

	@FXML
	private Label resourceLabel;
	@FXML
	private Hyperlink hyper1;
	@FXML
	private Button btn1;

	private TopicInfo topicInfo;

	/**
	 * Constructor of the controller.
	 * 
	 * @param resourceUrl The URL, specifying the location of the resource, as a
	 *                    {@link String}.
	 * @param label       The label of the resource in english as a {@link String}.
	 * @param wikiUrl     Url to the Wikipedia article of the given resource as
	 *                    string.
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

		resourceLabel.setText(topicInfo.getLabel());	
		hyper1.setText(info.getWikiUri());
		btn1.setText("add to my topics ");
		btn1.setOnAction(e -> {
			try {
				SWTApplication.getTopicManager().addResourceToTopics(topicInfo.getResourceUrl());
				SWTApplication.getTopicManager().getSuggestionsForCurrentTopic(SWTApplication.getNumberOfSuggestions());
			} catch (InvalidUriInputException e1) {
				// TODO add Alert?
				e1.printStackTrace();
			}
		});
		
		
	    resourceLabel.setOnMouseClicked((mouseEvent) -> {
            System.out.println("label clicked");
 
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
