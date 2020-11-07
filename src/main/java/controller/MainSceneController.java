package controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.jena.query.QuerySolution;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Controller class of the main {@link Scene} of the app. Handles every
 * interaction with the ui in this scene.
 * 
 */
public class MainSceneController implements Initializable {

	@FXML
	private Button btn1;
	@FXML
	private TextField text1;
	@FXML
	private TextFlow textArea1;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
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
		Class<?> calcClass;
		DBPediaNav db;
		try {
			calcClass = Class.forName("DBPediaNavigator");
			db = (DBPediaNav) calcClass.newInstance();
			db.registerNewResource(s);
			db.setNumber(10);
			//vorher
			List<QuerySolution> result = db.findNextProposals(db.getPreviousResources().iterator().next());
			
			int count = 0;
			StringBuffer sb = new StringBuffer();
			for (QuerySolution qs : result) {
				sb.append(qs+"\n");
				count++;
			}
			Text erg = new Text(sb.toString());
			textArea1.getChildren().addAll(erg);
			
			System.out.println(count);
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
