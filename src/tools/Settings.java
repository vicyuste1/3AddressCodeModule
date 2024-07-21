package tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * @author Victor Yuste Vara
 * Singleton class that holds and manages all program settings
 */
public class Settings {

	/**
	 * Attributes
	 */
	
	private static Settings singleInstance;
	
	private FileImporter fileImporter;
	
	private ArrayList<String> settings;
	
	private ResourceBundle bundle;
	
	private String lang;
	
	/**
	 * Private constructor
	 */
	private Settings() {
		this.fileImporter = new FileImporter("src/tools/settings.conf");
		//System.out.println()
		this.fileImporter.importFile();
		settings = this.fileImporter.getImport();
		
		if(getSettingsLine("bundle=").equals("en")){
			bundle = ResourceBundle.getBundle("bundles.langBundle_en");
			lang = "en";
		} else {
			bundle = ResourceBundle.getBundle("bundles.langBundle_es");
			lang = "es";
		}
		
		
	}

	/**
	 * Retrieves the Singleton instance
	 * @return the Singleton instance of the class
	 */
	public static synchronized Settings getInstance(){
		
		if(singleInstance == null) {
			singleInstance = new Settings();
		} 
		
		return singleInstance;
	}
	
	/**
	 * Returns the settings of an specific Settings line
	 * @return Returns the substring with the content of the settings
	 */
	public String getSettingsLine(String s) {
		
		String line = null;
		for(String l : settings) {
			if(l.contains(s)) {
				String cut[] = l.split(" ", 2);
				line = cut[1];
			}
		}
		return line;
		
	}
	
	/**
	 * Changes the language in the settings file
	 * @param language String with the chosen language
	 */
	public void changeLanguage(String language) {
		
		int index = 0;
		if(language.equals("en")) {
			for(String s : settings) {
				if(s.contains("bundle=")) {
					settings.set(index, "bundle= en");
				}
				index++;
			}
		} else {
			for(String s : settings) {
				if(s.contains("bundle=")) {
					settings.set(index, "bundle= es");
				}
				index++;
			}
		}
		
		saveSettings();
	}
	
	/**
	 * Changes the name of a specific App in the settings file
	 * @param appNumber Number of the App
	 * @param appName New name of the App
	 */
	public void changeAppName(int appNumber, String appName) {
		
		String nameLine = "app" + appNumber + "_name= ";
		int index = 0;
		
		for(String s : settings) {
			if(s.contains(nameLine)) {
				settings.set(index, nameLine + appName);
				break;
			}
			index++;
		}
		
		saveSettings();
	}
	
	/**
	 * Changes the image of a specific App in the settings file
	 * @param appNumber Number of the App
	 * @param image New file for the App image
	 */
	public void changeImage(int appNumber, File image) {
		
		if(image == null) {
			
			int index = 0;
			String imageLine = "app" + appNumber + "_image= ";			
			for(String s : settings) {
				if(s.contains(imageLine)) {
					settings.set(index, imageLine + "null");
					break;
				}
				index++;
			}
			
			saveSettings();
			return;
		}
		
		File dest = new File("./bin/resources/" + image.getName());
		try {
			Files.copy(image.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle(bundle.getString("IOErrorSaveImageTitle"));
			alert.setHeaderText(bundle.getString("IOErrorSaveImage"));
			alert.show();
			
		}
		
		String imageLine = "app" + appNumber + "_image= ";
		int index = 0;
		String imagePath = "/resources/" + dest.getName();
		
		for(String s : settings) {
			if(s.contains(imageLine)) {
				imageLine = imageLine + imagePath;
				settings.set(index, imageLine);
				break;
			}
			index++;
		}
		
		saveSettings();
	}
	
	/**
	 * Changes the executable file of a specific App in the settings file
	 * @param appNumber Number of the App
	 * @param file New executable file for the App 
	 */
	public void changeFile(int appNumber, File file) {
		
		if(file == null) {
			
			int index = 0;
			String fileLine = "app" + appNumber + "_file= ";			
			for(String s : settings) {
				if(s.contains(fileLine)) {
					settings.set(index, fileLine + "null");
					break;
				}
				index++;
			}
			
			saveSettings();
			return;
		}
		
		String fileLine = "app" + appNumber + "_file= ";
		int index = 0;
		String filePath = file.getAbsolutePath();
		
		for(String s : settings) {
			if(s.contains(fileLine)) {
				fileLine = fileLine + filePath;
				settings.set(index, fileLine);
				break;
			}
			index++;
		}
		
		saveSettings();
	}
	
	/**
	 * Writes the current settings into the settings file
	 */
	private void saveSettings() {
		try {
			BufferedWriter br = new BufferedWriter(new FileWriter("src/tools/settings.conf"));
			for(String s : settings) {
				br.write(s);
				br.newLine();
			}
			br.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Checks if an App has an image associated in the settings
	 * @param appNumber Number of the App
	 */
	public boolean imageExists(int appNumber) {
		
		String imageLine = "app" + appNumber + "_image=";
		boolean exists = false;
		
		for(String s : settings) {
			if(s.contains(imageLine)) {
				imageLine = getSettingsLine(imageLine);
				
				if(!imageLine.equals("null")) {
					exists = true;
				}
			}
		}
		
		return exists;
	}
	
	/**
	 * Checks if an App has an executable file associated in the settings
	 * @param appNumber Number of the App
	 */
	public boolean fileExists(int appNumber) {
		
		String fileLine = "app" + appNumber + "_file=";
		boolean exists = false;
		
		for(String s : settings) {
			if(s.contains(fileLine)) {
				fileLine = getSettingsLine(fileLine);
				
				if(!fileLine.equals("null")) {
					exists = true;
				}
			}
		}
		
		return exists;
	}
	
	/**
	 * Checks if an App has a name associated in the settings
	 * @param appNumber Number of the App
	 */
	public boolean nameExists(int appNumber) {
		
		String nameLine = "app" + appNumber + "_name=";
		boolean exists = false;
		
		for(String s : settings) {
			if(s.contains(nameLine)) {
				nameLine = getSettingsLine(nameLine);
				
				if(!nameLine.equals("null")) {
					exists = true;
				}
			}
		}
		
		return exists;
	}
	
	
	
	/**
	 * Setters and getters
	 */
	
	public ArrayList<String> getSettings() {
		return settings;
	}

	public void setSettings(ArrayList<String> settings) {
		this.settings = settings;
	}

	public ResourceBundle getBundle() {
		return bundle;
	}

	public void setBundle(ResourceBundle bundle) {
		this.bundle = bundle;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}
}
