package application;

import javafx.scene.control.Button;

import java.io.File;

import compress.Compress;
import encrypt.AES;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import serverConnection.ServerConnection;

class Cell extends ListCell<String> {
	
	private String directory;
	private String name;
	
	HBox hbox = new HBox();
	Button metaViewBtn = new Button("View Metadata");
	Button downloadBtn = new Button("Download");
	Button decryptBtn = new Button("Decrypt");
	Button deleteBtn = new Button("Delete");
	Label label = new Label("");
	Pane pane = new Pane();
	
	public Cell(String directory, boolean local) {
		super();
		
		if(local) {
			this.directory = directory;
			hbox.getChildren().addAll(label,pane,metaViewBtn,decryptBtn,deleteBtn);
			hbox.setHgrow(pane, Priority.ALWAYS);

			

			deleteBtn.setOnAction(e ->  deleteFile());
			metaViewBtn.setOnAction(e -> viewMetaData());
			decryptBtn.setOnAction(e -> decryptEnc());
		}
		else {
			this.directory = directory;
			this.name = directory;
			System.out.println("asignando "  + this.name);
			hbox.getChildren().addAll(label,pane,downloadBtn,deleteBtn);
			hbox.setHgrow(pane, Priority.ALWAYS);

			

			deleteBtn.setOnAction(e ->  deleteFileServer());
			downloadBtn.setOnAction(e -> downloadData());
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
		
		//System.out.println(path);
		/*if (ServerConnection.downloadFiles(path,username, zipName))
			System.out.println("Upload succesfuly");
		else
			System.out.println("Error uploading");*/
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
	
	public void deleteFileServer() {
		
	}
	
}
