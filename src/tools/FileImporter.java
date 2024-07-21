package tools;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 
 */

/**
 * @author Victor Yuste Vara
 * Class that imports text files as a list of Strings
 */
public class FileImporter {

	private BufferedReader br;
	private File file;
	private ArrayList<String> lines;
	
	/**
	 * Public constructor
	 * @param filePath String with the path of the file
	 */
	public FileImporter(String filePath) {
		this.file = new File(filePath);
		this.lines = new ArrayList<>();
		try {
			this.br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			System.out.println("Error al abrir el archivo");
			e.printStackTrace();
		}	
	}
	
	/**
	 * Public constructor
	 * @param file The File that needs to be read
	 */
	public FileImporter(File file) {
		this.file = file;
		this.lines = new ArrayList<>();
		try {
			this.br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			System.out.println("Error al abrir el archivo");
			e.printStackTrace();
		}	
	}
	
	/**
	 * Stores the content of the file given in the constructor in an ArrayList of Strings
	 */
	public void importFile() {
		String line;
		try {
			while((line = br.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException e) {
			System.out.println("Error al leer el archivo");
			e.printStackTrace();
		}
	}
	
	/**
	 * Recovers the imported file
	 * @return Returns the ArrayList with the content of the file
	 */
	public ArrayList<String> getImport(){
		return this.lines;
	}
	
	
	
}
