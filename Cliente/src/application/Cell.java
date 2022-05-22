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
import javafx.stage.Modality;
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
	Button signCheckBtn = new Button("Check Signature");
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
			hbox.getChildren().addAll(label,pane,downloadBtn,signCheckBtn,shareBtn);
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
			signCheckBtn.setOnAction(e -> {
				try {
					checkSignature();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
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
	
	public String[] sharedFileSeparator(String name) {

		String[] aux = name.split("->");
		
		if(aux.length != 1) {
			String[] aux2 = aux[1].split(":");
			String[] result = {aux[0], aux2[1]};

			return result;
		}
		else {
			String[] result = {aux[0]};
			return result;
		}
		
	}
	
	public String obtainCustomPath() {
		
		String filePath = Data.dirPath + File.separator + getItem();
		
		return filePath;
		
	}
	
	public void checkSignature() throws IOException {
		//Implementa aqui la logica
		
		//Pon aqui el mensaje que quieras
		Data.alertMsg = "";
		
		//Calling to Alert Window
		stage = new Stage();
		root = FXMLLoader.load(getClass().getResource("AlertPopup.fxml"));
		stage.setScene(new Scene(root));
		stage.setTitle("Signature check");
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(getScene().getWindow());
		stage.showAndWait();
	}
	
	public void downloadData() {
		String path = obtainCustomPath();
		String username = Data.username;
		
		//.lenght sera o 1 o 2 segun si es shared o no
		String[] sharedInfo = sharedFileSeparator(getItem());
		
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
			if(sharedInfo.length == 1) {
				if (ServerConnection.downloadFiles(local, username, fileName))
					System.out.println("Upload succesfuly");
				else
					System.out.println("Error uploading");
			}
			else {
				String filename = sharedInfo[0];
				String sharingUser = sharedInfo[1];
				
				
			}
			
		}
	}
	
	public void viewMetaData() {
		
		Compress meta = new Compress();
		String path = obtainCustomPath();
		
		try {
	        // when button is pressed
			String rawMeta = meta.showMeta(path);
			
			Data.metaString = rawMeta.substring(0, rawMeta.length() -2);
			
			stage = new Stage();
			root = FXMLLoader.load(getClass().getResource("MetaView.fxml"));
			stage.setScene(new Scene(root));
			stage.setTitle(getItem());
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initOwner(getScene().getWindow());
			stage.showAndWait();
			
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
		
		String[] sharedInfo = sharedFileSeparator(getItem());
		
		if(sharedInfo.length == 1) {
			Data.fileToShareName = getItem();
			
			Parent root = FXMLLoader.load(getClass().getResource("ShareFile.fxml"));

			stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		}
		else {
			Data.alertMsg = "Cannot share a file that isn't yours";
			
			stage = new Stage();
			root = FXMLLoader.load(getClass().getResource("AlertPopup.fxml"));
			stage.setScene(new Scene(root));
			stage.setTitle("Warning");
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initOwner(getScene().getWindow());
			stage.showAndWait();
		}
		
	}
	
	public void deleteFileServer() {
		
	}
	
}
