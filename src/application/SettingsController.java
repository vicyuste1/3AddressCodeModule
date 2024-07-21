package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import tools.Settings;

/**
 * @author Victor Yuste Vara
 * Class that controls the Settings view
 */
public class SettingsController implements Initializable{
	
	/**
	 * Attributes
	 */
	
	@FXML
	private ChoiceBox<String> languageBox;
	
	private ResourceBundle bundle;
	
	private Settings settings;
	
	private String lang;
	
	private ArrayList<String> languages = new ArrayList<>();
	
	/**
	 * Initializes the controller
	 * @param URL 
	 * @param arg1 
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		settings = Settings.getInstance();
		bundle = settings.getBundle();
		lang = settings.getLang();
		
		if(lang.equals("en")) {
			languages.add("English");
			languages.add("Spanish");
		} else {
			languages.add("Inglés");
			languages.add("Español");
		}
		
		languageBox.getItems().addAll(languages);
		languageBox.setOnAction(this::changeLanguage);
		
	}
	
	/**
	 * Changes the Locale Bundle to the given one
	 * @param bundle Selected bundle 
	 */
	public void setBundle(ResourceBundle bundle) {
		this.bundle = bundle;
	}
	
	/**
	 * Changes the Settings Language to the chosen one by the user
	 * @param event GUI event
	 */
	public void changeLanguage(ActionEvent event) {
		
		String choice = languageBox.getValue();
		
		if(choice.equals("English") || choice.equals("Inglés")) {
			settings.changeLanguage("en");
		} else {
			settings.changeLanguage("es");
		}
		
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(bundle.getString("changeLangTitle"));
		alert.setHeaderText(bundle.getString("changeLangText"));
		alert.show();
	}
	
	

}
