package ui.controller;

import backend.exception.InvalidUriInputException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.jena.query.QuerySolution;

import backend.TopicManager;
import backend.TopicManagerImpl;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextFlow;

/**
 * Controller class of the main {@link Scene} of the app. Handles every
 * interaction with the ui in this scene.
 */
public class MainSceneController implements Initializable {

	private static final int NUM_OF_SUGGESTIONS = 15; // TODO let the user change this

	@FXML
	private AnchorPane proposedTopicBase;
	@FXML
	private Button btn1;
	@FXML
	private TextField text1;
	@FXML
	private TextFlow textArea1;

	private TopicManager topicManager;
	private ProposedTopicList proposedTopicList;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		this.topicManager = new TopicManagerImpl();
		this.proposedTopicList = new ProposedTopicList();
		this.proposedTopicBase.getChildren().add(proposedTopicList.getRoot());
		
		// run search on enter
		this.text1.setOnKeyPressed(e -> {
			if(e.getCode().equals(KeyCode.ENTER)) {
				onClick();
			}
		});
	}

	public String onClick() {
		String text = text1.getText();
		setResult(text);
		return text;
	}

	public void setResult(String s) {
		// textArea1.setText(s);
		// register the new Resource
		try {
			topicManager.addResourceToTopics(TopicManagerImpl.RESOURCE_URI + s);
		} catch (InvalidUriInputException e) {
			e.printStackTrace();
		}
		// vorher
		List<QuerySolution> result = topicManager.getSuggestionsForCurrentTopic(NUM_OF_SUGGESTIONS);
		
		proposedTopicList.clearAndPopulateList(result);
		
		
		int count = 0;
//		StringBuffer sb = new StringBuffer();
//		for (QuerySolution qs : result) {
//			sb.append(qs + "\n");
//			count++;
//		}
//		Text erg = new Text(sb.toString());
//		textArea1.getChildren().addAll(erg);

	}

}
