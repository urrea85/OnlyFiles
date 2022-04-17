package application;

import javafx.scene.control.Button;
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
	//private String name;
	
	HBox hbox = new HBox();
	Button metaViewBtn = new Button("View Metadata");
	Button uploadBtn = new Button("Upload");
	Button downloadBtn = new Button("Download");
	Button decryptBtn = new Button("Decrypt");
	Button deleteBtn = new Button("Delete");
	Label label = new Label("");
	Pane pane = new Pane();
	
	public Cell(String directory, boolean local) {
		super();
		
		if(local) {
			this.directory = directory;
			hbox.getChildren().addAll(label,pane,uploadBtn,metaViewBtn,decryptBtn,deleteBtn);
			hbox.setHgrow(pane, Priority.ALWAYS);

			

			deleteBtn.setOnAction(e ->  deleteFile(this.directory));
			metaViewBtn.setOnAction(e -> viewMetaData(this.directory));
			decryptBtn.setOnAction(e -> decryptEnc(this.directory));
			uploadBtn.setOnAction(e -> uploadData(this.directory));
		}
		else {
			this.directory = directory;
			hbox.getChildren().addAll(label,pane,downloadBtn,deleteBtn);
			hbox.setHgrow(pane, Priority.ALWAYS);

			

			deleteBtn.setOnAction(e ->  deleteFileServer());
			uploadBtn.setOnAction(e -> downloadData(this.directory));
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
	
	public void downloadData(String path) {

	}
	
	public void uploadData(String path) {

	}
	
	public void viewMetaData(String path) {
		
		Compress meta = new Compress();
		try {
	        // when button is pressed
			meta.showMeta(path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void decryptEnc(String path) {
		AES aes = new AES();
		Compress unzip = new Compress();
		try {
			aes.decryptController(path);
			unzip.unzip(path.replace(".encrypt", ".zip"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void deleteFile(String path) {
		Compress file = new Compress();
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
