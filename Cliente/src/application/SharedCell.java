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

public class SharedCell extends ListCell<String>{
	
	private String name;
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	HBox hbox = new HBox();
	Button downloadBtn = new Button("Download");
	Button deleteBtn = new Button("Delete");
	Label label = new Label("");
	Pane pane = new Pane();
	
	public SharedCell(String name) {
		super();
		
		this.name = name;
		hbox.getChildren().addAll(label,pane,downloadBtn);
		hbox.setHgrow(pane, Priority.ALWAYS);

		downloadBtn.setOnAction(e -> downloadSharedData());
		
	}
	
	public void downloadSharedData() {
		
	}

}
