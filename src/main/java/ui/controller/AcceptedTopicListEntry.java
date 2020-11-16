package ui.controller;


import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;

/**
 * Controller class for an entry in the list of proposed topics. Contains
 * details such as the link to Wikipedia and a button to add the element to the
 * topics, that the user accepted.
 */
public class AcceptedTopicListEntry {

	private Parent root;

	@FXML
	private Label resourceLabel;

	private String label, url;

	/**
	 * Constructor of the controller.
	 * 
	 * @param resourceUrl The URL, specifying the location of the resource, as a
	 *                    {@link String}.
	 * @param label       The label of the resource in english as a {@link String}.
	 * @param wikiUrl     Url to the Wikipedia article of the given resource as
	 *                    string.
	 */
	public AcceptedTopicListEntry(String resourceUrl, String label) {
		/*
		 * Store attributes in the controller for later use. 
		 */
		this.label = label;
		this.url = resourceUrl;

		// load the fxml file for the entry
		FXMLLoader loader = new FXMLLoader(
				getClass().getClassLoader().getResource("fxml/accepted_topic_list_entry.fxml"));
		loader.setController(this);
		try {
			this.root = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		resourceLabel.setText(label);	
	}

	public Parent getRoot() {
		return root;
	}	
}
