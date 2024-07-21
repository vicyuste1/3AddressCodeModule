package application;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import parser.Parser;
import tools.Settings;

/**
 * @author Victor Yuste Vara
 * Main class
 */
public class MainController implements Initializable{

	/**
	 * Attributes
	 */
	
	@FXML
	private MenuItem closeButton, saveButton, helpButton, languageButton;
	
	@FXML 
	private Button translateButton;
		
	@FXML 
	private ListView<String> listView;
	
	@FXML 
	private TextField textField;
	
	private Settings settings;
	
	private String lang;
	
	private ResourceBundle bundle;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		settings = Settings.getInstance();
		bundle = settings.getBundle();
		lang = settings.getLang();
		
		textField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background,-30%); }");
		
	}
	
	/**
	 * Closes the Program with all its views
	 * @param event GUI event
	 */
	public void close(ActionEvent event) {
		
		Platform.exit();
		
	}
	
	/**
	 * Opens the Help view
	 * @param event GUI event
	 */
	public void openHelp(ActionEvent event) {
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Help.fxml"), bundle);
			Parent root = loader.load();
			HelpController helpController = loader.getController();
			helpController.setBundle(bundle);
			Scene scene = new Scene(root);
			Image logo = new Image("file:src/resources/appLogo.png");
			Stage helpStage = new Stage();
			helpStage.setTitle("Help");
			helpStage.getIcons().add(logo);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			helpStage.setScene(scene);
			helpStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Opens the Settings view
	 * @param event GUI event
	 */
	public void openSettings(ActionEvent event) {
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Settings.fxml"), bundle);
			Parent root = loader.load();
			SettingsController settingsController = loader.getController();
			settingsController.setBundle(bundle);
			Scene scene = new Scene(root);
			Image logo = new Image("file:src/resources/appLogo.png");
			Stage settingsStage = new Stage();
			settingsStage.setTitle("Settings");
			settingsStage.getIcons().add(logo);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			settingsStage.setScene(scene);
			settingsStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Translates the text given by the user
	 * @param event GUI event
	 */
	public void translate(ActionEvent event) {

		String original = textField.getText();
		ArrayList<String> translation = new ArrayList<>();
		try {
			Parser parser = new Parser(original);
			translation = parser.translate();
		} catch(Exception e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle(bundle.getString("translationErrorTitle"));
			alert.setHeaderText(bundle.getString("translationErrorText"));
			alert.show();
			return;
		}
				
		listView.getItems().clear();
		listView.getItems().addAll(translation);
		//ObservableList<String> temp = listView.getItems();
	}
	
	/**
	 * Saves the result of the translation in a file
	 * @param event GUI event
	 */
	public void save(ActionEvent event) {
		
		try {
			BufferedWriter br = new BufferedWriter(new FileWriter("Translation.txt"));
			ObservableList<String> lines = listView.getItems();
			for(String line : lines) {
				br.write(line);
				br.newLine();
			}
			br.close();
			
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle(bundle.getString("saveFileOKTitle"));
			alert.setHeaderText(bundle.getString("saveFileOK"));
			alert.show();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
