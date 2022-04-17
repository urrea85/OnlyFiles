package application;

import java.io.File;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class fileController implements Initializable{
	
	@FXML
	private ListView<String> listView;
		
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	private String dirPath;
	private File directory;
	private File[] files;
	
	private ArrayList<File> compressedFiles;

	
	//Gets file extension, returns null if the file has no extension
	public Optional<String> getExtension(String filename) {
	    return Optional.ofNullable(filename)
	      .filter(f -> f.contains("."))
	      .map(f -> f.substring(filename.lastIndexOf(".") + 1));
	}
	
	public void updateListView(String dirPath) {
		
		compressedFiles = new ArrayList<File>();
		
		directory = new File(dirPath);
		
		files = directory.listFiles();
		
		if(files != null) {
			for(File file : files) {
				
				Optional<String> extension = getExtension(file.getName());
				
				//Only compressed files will be displayed
				if(extension.isPresent() && extension.get().equals("encrypt")) {
							
					compressedFiles.add(file);
					
					listView.getItems().addAll(file.getName());
					listView.setCellFactory(param -> new Cell(file.getAbsolutePath()));
				}
				
			}
		}
		
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		
		if(Data.dirPath == "") {
			System.out.println(dirPath);
		}
		else {
			this.dirPath = Data.dirPath;
			
			updateListView(dirPath);
		}
		
	}
	
	public void setFolder(ActionEvent e) {
		
		DirectoryChooser dC = new DirectoryChooser();
		
		File selectedDirectory = dC.showDialog(null);
		
		dirPath = selectedDirectory.getAbsolutePath();
		
		Data.dirPath = dirPath;

		updateListView(dirPath);
						
	}
	
	public void switchToNewFile(ActionEvent event) throws IOException {
		
		Parent root = FXMLLoader.load(getClass().getResource("NewFile.fxml"));

		stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		
	}
	
	public void refreshList(ActionEvent event) {
		
	}
	
	public void logout(ActionEvent event) throws IOException {
		
		Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));

		stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}


}
