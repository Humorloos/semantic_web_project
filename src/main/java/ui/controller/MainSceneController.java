package ui.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.SWTApplication;
import backend.TopicManagerImpl;
import backend.exception.InvalidUriInputException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextFlow;
import model.TopicInfo;
import ui.util.TextFieldConfigurator;

/**
 * Controller class of the main {@link Scene} of the app. Handles every
 * interaction with the ui in this scene.
 */
public class MainSceneController implements Initializable {

	public static final int MAX_NUM_OF_SUGGESTIONS = 50;
	private int numOfRequestedSuggestions = 15;

	@FXML
	private AnchorPane proposedTopicBase, acceptedTopicBase;
	@FXML
	private Button btn1;
	@FXML
	private TextField text1;
	@FXML
	private TextFlow textArea1;
	@FXML
	private TextField numberArea;

	private ProposedTopicList proposedTopicList;
	private AcceptedTopicList acceptedTopicList;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		SWTApplication.setTopicManager(new TopicManagerImpl());
		this.acceptedTopicList = new AcceptedTopicList();
		this.proposedTopicList = new ProposedTopicList();
		this.proposedTopicBase.getChildren().add(proposedTopicList.getRoot());
		this.acceptedTopicBase.getChildren().add(acceptedTopicList.getRoot());

		// run search on enter
		this.text1.setOnKeyPressed(e -> {
			if (e.getCode().equals(KeyCode.ENTER)) {
				onClick();
			}
		});
		this.text1.requestFocus();
		numberArea.setText(numOfRequestedSuggestions + "");
		TextFieldConfigurator.configureNumericTextField(numberArea, MAX_NUM_OF_SUGGESTIONS);
		TextFieldConfigurator.configureUrlTextField(text1);
	}

	public String onClick() {
		String text = text1.getText();
		setResult(text);
		return text;
	}

	/**
	 * Add a new topic to the list of accepted topics.
	 * 
	 * @param topic The {@link TopicInfo} for the topic to add.
	 */
	public void addTopicToAcceptedTopics(TopicInfo topic) {
		this.proposedTopicList.removeTopic(topic.getResourceUrl());
		this.acceptedTopicList.addTopic(topic);
	}

	public void removeTopicAcceptedTopics(TopicInfo topic) {
		this.acceptedTopicList.removeTopicEntry(topic.getResourceUrl());
	}

	public void setResult(String s) {
		// textArea1.setText(s);
		// register the new Resource
		try {
			String resourceUrl = TopicManagerImpl.RESOURCE_URI + s;
			String label = SWTApplication.getTopicManager().addResourceToTopics(resourceUrl);
			TopicInfo info = new TopicInfo(resourceUrl, label, "", "", ""); //TODO add type of resource?
			this.acceptedTopicList.addTopic(info);
		} catch (InvalidUriInputException e) {
			Alert a = new Alert(Alert.AlertType.ERROR, "Invalid Input");
			a.showAndWait();
		}

		try {
			numOfRequestedSuggestions = Integer.parseInt(numberArea.getText());
		} catch (NumberFormatException e) {
			Alert a = new Alert(Alert.AlertType.ERROR, "Please give a number");
			a.showAndWait();
		}
		// vorher
		List<TopicInfo> result = SWTApplication.getTopicManager()
				.getSuggestionsForPreviousResources(numOfRequestedSuggestions);

		proposedTopicList.clearAndPopulateList(result);

	}

}
