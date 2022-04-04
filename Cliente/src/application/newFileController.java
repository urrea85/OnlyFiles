package application;

import java.io.File;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.json.simple.JSONObject;

import compress.Compress;
import encrypt.AES;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class newFileController implements Initializable{
		
	//New file
	@FXML
	private ListView<String> fileView;
	
	@FXML
	private TextField fileNameTextField;
	
	@FXML
	private TextField ownerNameTextField;
	
	@FXML
	private TextField descriptionTextField;
	
	@FXML
	private DatePicker fileDatePicker;
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	private List<File> selectedFiles;
	private String dirPath;
	private File directory;
	private File[] files;
	
	private List<List<String>> metadata;
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		
		this.dirPath = Data.dirPath;
		directory = new File(dirPath);
				
	}
	
	public void selectFiles(ActionEvent e) {
		selectedFiles = new ArrayList<File>();
				
		FileChooser fC = new FileChooser();
		
		fC.setInitialDirectory(directory);
		
		selectedFiles = fC.showOpenMultipleDialog(null);
		
		if(selectedFiles != null) {
			for(int i = 0; i < selectedFiles.size(); i++) {
							
				fileView.getItems().add(selectedFiles.get(i).getName());
					
			}
		}

	}
	
	public List<String> obtainFilesPath(List<File> selectedFiles, String metaPath){
		List<String> filesPath = new ArrayList<String>();
		
		for(int i = 0; i < selectedFiles.size(); i++) {
			filesPath.add(selectedFiles.get(i).getAbsolutePath());
		}
		
		filesPath.add(metaPath);
		
		return filesPath;
	}
	
	public void createNewFile(ActionEvent e) {
				
		String fileName = fileNameTextField.getText();
		String ownerName = ownerNameTextField.getText();
		String description = descriptionTextField.getText();
		LocalDate date = fileDatePicker.getValue();
		
		List<String> auxName = Arrays.asList("File Name", fileName);
		List<String> auxOwner = Arrays.asList("Owner", ownerName);
		List<String> auxDesc = Arrays.asList("Description", description);
		List<String> auxDate = Arrays.asList("Date", date.toString());
		
		metadata = Arrays.asList(auxName, auxOwner, auxDesc, auxDate);
		
		Compress compress = new Compress();
		JSONObject json = compress.metadata(metadata);
		
		String tempName = dirPath + File.separator + fileName + "meta.json";
		
		String zipName = dirPath + File.separator + fileName + ".zip";
		
		compress.metaToFile(json, tempName);
		AES aes = new AES();
		
		List<String> paths = obtainFilesPath(selectedFiles, tempName);
		
		try {
			compress.zip(paths, zipName);
			aes.encryptController(zipName);
			
			Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));

			stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	public void cancel(ActionEvent e) throws IOException {
		
		Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));

		stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();

		
	}


}
