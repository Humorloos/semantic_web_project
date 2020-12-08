package ui.controller;

import java.net.URL;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.ResourceBundle;

import application.SWTApplication;
import backend.TopicManagerImpl;
import backend.exception.InvalidUriInputException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextFlow;
import javafx.stage.StageStyle;
import model.TopicInfo;
import ui.util.TextFieldConfigurator;

/**
 * Controller class of the main {@link Scene} of the app. Handles every
 * interaction with the ui in this scene.
 */
public class MainSceneController implements Initializable {

	public static final int MAX_NUM_OF_SUGGESTIONS = 50;

	public int getNumOfRequestedSuggestions() {
		return numOfRequestedSuggestions;
	}

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
	@FXML
	private ProgressIndicator indicator;

	public ProgressIndicator getProgressIndicator() {
		return indicator;
	}

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
				// onClick();
				new Thread(createLoadTask()).start();
			}
		});
		this.btn1.setOnAction(e -> {
			new Thread(createLoadTask()).start();
		});
		indicator.progressProperty().unbind();
		indicator.setVisible(false);

		this.text1.requestFocus();
		numberArea.setText(numOfRequestedSuggestions + "");
		TextFieldConfigurator.configureNumericTextField(numberArea, MAX_NUM_OF_SUGGESTIONS);
		TextFieldConfigurator.configureUrlTextField(text1);
	}

	private Task<Void> createLoadTask() {
		Task<Void> loadTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				indicator.setVisible(true);
				onClick();
				return null;
			}
		};
		loadTask.setOnSucceeded(e -> {
			Platform.runLater(() -> {
				fetchNewSuggestions();
				indicator.setVisible(false);
			});
		});
		loadTask.setOnFailed(e -> {
			loadTask.getException().printStackTrace();
		});
		return loadTask;
	}

	public void onClick() {
		String text = text1.getText();
		setResult(text);
	}

	/**
	 * Add a new topic to the list of accepted topics.
	 * 
	 * @param topic The {@link TopicInfo} for the topic to add.
	 */
	public void addTopicToAcceptedTopics(TopicInfo topic) {
//		this.proposedTopicList.removeTopic(topic.getResourceUrl()); Not necessary as the list is cleared anyways
		this.acceptedTopicList.addTopic(topic);
	}

	public void removeTopicAcceptedTopics(TopicInfo topic) {
		this.acceptedTopicList.removeTopicEntry(topic.getResourceUrl());
		this.fetchNewSuggestions();
	}

	public void setResult(String s) {
		// register the new Resource
		try {
			String resourceUrl = TopicManagerImpl.RESOURCE_URI + s;
			String label = SWTApplication.getTopicManager().addResourceToTopics(resourceUrl);
			Platform.runLater(() -> {
				TopicInfo info = new TopicInfo(resourceUrl, label, "", "", "", 0);
				this.acceptedTopicList.addTopic(info);
			});

		} catch (InvalidUriInputException | InvalidParameterException e) {
			Platform.runLater(() -> {
				Alert a = new Alert(Alert.AlertType.ERROR,
						"If you have trouble entering a topic, check its Wikipedia-Url and copy everything after 'https://en.wikipedia.org/wiki/'.");
				a.initStyle(StageStyle.UNIFIED);
				a.getDialogPane().getStylesheets()
						.add(SWTApplication.class.getResource("/css/general.css").toExternalForm());
				a.setHeaderText(e.getMessage());
				a.showAndWait();
			});
		}

		try {
			numOfRequestedSuggestions = Integer.parseInt(numberArea.getText());
		} catch (NumberFormatException e) {
			Alert a = new Alert(Alert.AlertType.ERROR, "Please give a number");
			a.showAndWait();
		}
	}

	public void fetchNewSuggestions() {
		List<TopicInfo> result = SWTApplication.getTopicManager()
				.getSuggestionsForPreviousResources(numOfRequestedSuggestions);
		proposedTopicList.clearAndPopulateList(result);
	}

	public ProposedTopicList getproposedTopicList() {
		return this.proposedTopicList;
	}

	public TopicInfo getProposedTopicInfo(String resourceUrl) {
		return acceptedTopicList.getTopic(resourceUrl);
	}

}
