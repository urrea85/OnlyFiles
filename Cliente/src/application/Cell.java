package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import compress.Compress;
import encrypt.AES;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import serverConnection.ServerConnection;

class Cell extends ListCell<String> {
	
	private String directory;
	private String name;
	private String aux;
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	HBox hbox = new HBox();
	Button metaViewBtn = new Button("View Metadata");
	Button downloadBtn = new Button("Download");
	Button shareBtn = new Button("Share");
	Button decryptBtn = new Button("Decrypt");
	Button deleteBtn = new Button("Delete");
	Label label = new Label("");
	Pane pane = new Pane();
	
	public Cell(String directory, boolean local) {
		super();
		
		if(local) {
			this.directory = directory;
			Data.auxPath = directory;
			hbox.getChildren().addAll(label,pane,metaViewBtn,decryptBtn,deleteBtn);
			hbox.setHgrow(pane, Priority.ALWAYS);

			

			deleteBtn.setOnAction(e ->  deleteFile());
			metaViewBtn.setOnAction(e -> viewMetaData());
			decryptBtn.setOnAction(e -> decryptEnc());
		}
		else {
			this.directory = directory;
			this.name = directory;
			hbox.getChildren().addAll(label,pane,downloadBtn,shareBtn,deleteBtn);
			hbox.setHgrow(pane, Priority.ALWAYS);

			deleteBtn.setOnAction(e ->  deleteFileServer());
			downloadBtn.setOnAction(e -> downloadData());
			shareBtn.setOnAction(e -> {
				try {
					shareFileServer(e);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			});
		}
	}
	
	public void updateItem(String name, boolean empty) {
		
		super.updateItem(name, empty);
		setText(null);
		setGraphic(null);
		
		if(name != null && !empty) {
			label.setText(name);
			setGraphic(hbox);	
		}
	}
	
	public String obtainCustomPath() {
		
		String filePath = Data.dirPath + File.separator + getItem();
		
		return filePath;
		
	}
	
	public void downloadData() {
		String path = obtainCustomPath();
		String username = Data.username;
		System.out.println(path);
		System.out.println(username);
		System.out.println(Data.auxPath);
		String local ="";
		String fileName="";
		String separator = Pattern.quote(File.separator);
		String[] paths = path.split(separator);
		fileName = paths[paths.length-1];
		local = path.replace(fileName, "");

		if(local.equals(File.separator)) {
			System.out.println("Selecciona el directorio local");
		}else {
			//local = path.replace(fileName, "");
			if (ServerConnection.downloadFiles(local, username, fileName))
				System.out.println("Upload succesfuly");
			else
				System.out.println("Error uploading");
		}
	}
	
	public void viewMetaData() {
		
		Compress meta = new Compress();
		String path = obtainCustomPath();
		
		try {
	        // when button is pressed
			meta.showMeta(path);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void decryptEnc() {
		AES aes = new AES();
		Compress unzip = new Compress();
		String path = obtainCustomPath();
		
		try {
			aes.decryptController(path);
			unzip.unzip(path.replace(".encrypt", ".zip"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void deleteFile() {
		
		Compress file = new Compress();
		String path = obtainCustomPath();
		
		try {
	        // when button is pressed
			file.deleteFile(path);
			getListView().getItems().remove(getItem());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void shareFileServer(ActionEvent e) throws IOException {
		
		Data.fileToShareName = getItem();
		
		Parent root = FXMLLoader.load(getClass().getResource("ShareFile.fxml"));

		stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void deleteFileServer() {
		
	}
	
}
