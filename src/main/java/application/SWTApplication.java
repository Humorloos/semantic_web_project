package application;

import java.io.IOException;

import org.apache.jena.query.ARQ;
import org.apache.jena.sys.JenaSystem;

import backend.TopicManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import ui.controller.MainSceneController;

/**
 * The {@link Application} class that starts the app and initializes all
 * relevant instances of {@link Scene}s and their controller-classes.
 */
public class SWTApplication extends Application {

	private static TopicManager topicManager;

	public static TopicManager getTopicManager() {
		return topicManager;
	}
	
	public static int getNumberOfSuggestions() {
		return mainController.getNumOfRequestedSuggestions();
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
	private static MainSceneController mainController;

	public static MainSceneController getMainController() {
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
		JenaSystem.init();
		ARQ.init();
		try {
			this.window = window;
			loadScenes();
			prepareWindow();
		} catch (IOException e) {
			System.err.println("Something went wrong while loading the Scenes: ");
			e.printStackTrace();
		}
	}

	private void prepareWindow() {
		// set Stage boundaries to visible bounds of the main screen
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		window.setX(primaryScreenBounds.getMinX());
		window.setY(primaryScreenBounds.getMinY());
		window.setWidth(primaryScreenBounds.getWidth());
		window.setHeight(primaryScreenBounds.getHeight());
		// disable exiting the fullscreen with ESCAPE, set it to shift + escape for the
		// Main menu

		window.setResizable(false);

		window.setScene(mainScene);
		window.show();
	}

}
