package application;
	
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import tools.Settings;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;

/**
 * @author Victor Yuste Vara
 * Main class
 */
public class Main extends Application {
	
	/**
	 * Starts the JavaFX application and the main view
	 * @param primaryStage Main view stage
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			Settings settings = Settings.getInstance();
			ResourceBundle bundle = settings.getBundle();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"), bundle);
			Parent root = loader.load();
			Scene scene = new Scene(root);
			Image logo = new Image("file:src/resources/appLogo.png");
			primaryStage.setTitle("3 Address Module");
			primaryStage.getIcons().add(logo);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setOnCloseRequest(event -> Platform.exit());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Launches the JavaFX application
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
