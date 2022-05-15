package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;
import serverConnection.ServerConnection;

public class shareFileController implements Initializable{
	
	@FXML
	private ListView<String> userList;
	
	@FXML
	private Label fileNameLabel;
	
	private String rawUsers;
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	public void updateUsersList(String raw) {
		
		String[] userArray = raw.split(" ");
		
		for(String user : userArray) {
			
			userList.getItems().addAll(user);
			
		}
		
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		
		fileNameLabel.setText(Data.fileToShareName);
		
		//Aqui llamas a la funcion para obtener el string de nombres desde el server
		//Y quitas esta linea
		rawUsers = ServerConnection.listUsers(Data.username);
		
		updateUsersList(rawUsers);
		
		userList.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE);
		

	}
	
	public void shareFile(ActionEvent e) throws IOException {
		
		String rawSelectedUsers = "";
		
		for(String user : userList.getSelectionModel().getSelectedItems()) {
			
			rawSelectedUsers = rawSelectedUsers + user + " ";
			ServerConnection.shareZip(Data.dirPath, Data.username, user, Data.fileToShareName);			
		}
		
		rawSelectedUsers = rawSelectedUsers.substring(0,rawSelectedUsers.length()-1);
		
		//Aqui llamas a tu funcion de conexion al server (quitale el sysout bro)
		System.out.println(rawSelectedUsers);
		
		//Once finished, returns to the main screen
		Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));

		stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void cancelShare(ActionEvent event) throws IOException {
		
		Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));

		stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		
	}
	

}
