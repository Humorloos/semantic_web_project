package application;

import java.io.IOException;

import backend.TopicManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The {@link Application} class that starts the app and initializes all
 * relevant instances of {@link Scene}s and their controller-classes.
 */
public class SWTApplication extends Application {
	
	private static TopicManager topicManager;

	public static TopicManager getTopicManager() {
		return topicManager;
	}

	/**
	 * Starts the {@link Application}.
	 * 
	 * @param args Optional arguments. (Not used)
	 */
	public static void main(String[] args) {
		launch(args);
	}
	public static void setTopicManager(TopicManager topicManager) {
		SWTApplication.topicManager = topicManager;
	}
	private Stage window;

	/**
	 * The main {@link Scene} of the application, where the user receives proposals
	 * and interacts with the knowledge model.
	 */
	private Scene mainScene;

	/**
	 * Controller class for the main scene.
	 */
	private Initializable mainController;

	public Initializable getMainController() {
		return mainController;
	}

	/**
	 * This method loads the {@link Scene}s from fxml and the controllers.
	 * 
	 * @throws IOException
	 */
	private void loadScenes() throws IOException {
		// Main Scene
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/app.fxml"));
		mainScene = new Scene(loader.load());
		mainController = loader.getController();
	}

	@Override
	public void start(Stage window) throws Exception {

		try {
			loadScenes();
		} catch (IOException e) {
			System.err.println("Something went wrong while loading the Scenes: ");
			e.printStackTrace();
		}

		window.setScene(mainScene);
		window.show();
	}

}
