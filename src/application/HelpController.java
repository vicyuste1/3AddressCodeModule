package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import tools.Settings;

/**
 * @author Victor Yuste Vara
 * Class that controls the Help view
 */
public class HelpController implements Initializable{
	
	/**
	 * Attributes
	 */
	
	@FXML
    private MenuItem close_btn;
	
	@FXML
	private Pane helpPane;
	
	@FXML
	private WebView webviewPane;
	
	@FXML
	private ListView<String> topicList;
	
	private Stage currentStage;
	
	private WebEngine webEngine; 
	
	private ArrayList<String> topics = new ArrayList<>();
	
	private int numberOfTopics = 2;
	
	private String currentTopic;
	
	private ResourceBundle bundle;
	
	private Settings settings;
	
	private String lang;
	
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
		
		for(int i=1; i<=numberOfTopics; i++) {
			String topic = "topic" + i;
			String recTopic = bundle.getString(topic);
			topics.add(recTopic);
		}
		
		topicList.getItems().addAll(topics);
		loadTopic1();
		topicList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				
				webEngine = webviewPane.getEngine();
				currentTopic = topicList.getSelectionModel().getSelectedItem();
				String path = "/resources/" + currentTopic + ".html";
				path = Main.class.getResource(path).toExternalForm();
				webEngine.load(path);
				
			}
			
		});
		
		 
	}
	
	/**
	 * Closes the Help view
	 * @param event GUI event
	 */
	public void closeHelp(ActionEvent event) {
		currentStage = (Stage) helpPane.getScene().getWindow();
		currentStage.close();
	}
	
	/**
	 * Changes the Locale Bundle to the given one
	 * @param bundle Selected bundle 
	 */
	public void setBundle(ResourceBundle bundle) {
		this.bundle = bundle;
	}
	
	/**
	 * Loads the first help topic into the view
	 */
	public void loadTopic1() {
		
		String path;
		webEngine = webviewPane.getEngine();
			if(lang.equals("en")) {
				path = "/resources/How to use.html"; 
			} else {
				path = "/resources/Como usar.html";
			}
		
		path = Main.class.getResource(path).toExternalForm();
		webEngine.load(path);
		
	}

}
