package controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.jena.query.QuerySolution;

import backend.TopicManager;
import backend.TopicManagerSampleImpl;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Controller class of the main {@link Scene} of the app. Handles every
 * interaction with the ui in this scene.
 * 
 */
public class MainSceneController implements Initializable {

	private static final int NUM_OF_SUGGESTIONS = 10; // TODO let the user change this

	@FXML
	private Button btn1;
	@FXML
	private TextField text1;
	@FXML
	private TextFlow textArea1;

	private TopicManager topicManager;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		this.topicManager = new TopicManagerSampleImpl();
	}

	public String onClick() {
		String text = text1.getText();
		System.out.println("u give: " + text);
		setResult(text);
		return text;
	}

	public void setResult(String s) {
		// textArea1.setText(s);
		// register the new Resource
		topicManager.addResourceToTopics(s);
		// vorher
		List<QuerySolution> result = topicManager.getSuggestionsForCurrentTopic(NUM_OF_SUGGESTIONS);

		int count = 0;
		StringBuffer sb = new StringBuffer();
		for (QuerySolution qs : result) {
			sb.append(qs + "\n");
			count++;
		}
		Text erg = new Text(sb.toString());
		textArea1.getChildren().addAll(erg);

		System.out.println(count);
	}

}
