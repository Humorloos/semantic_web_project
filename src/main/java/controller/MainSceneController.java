package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

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
	private TextArea textArea1;
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
	}
	
	public String onClick() {
		 String text = text1.getText();
		 System.out.println("u give: "+text);
		 setResult(text);
	     return text;
	}
	
	public void setResult(String s) {
		textArea1.setText(s);
	}

}
