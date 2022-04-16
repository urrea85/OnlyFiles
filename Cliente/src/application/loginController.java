package application;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import serverConnection.ServerConnection;

public class loginController {
	
	@FXML
	private TextField nicknameTextField;
	
	@FXML
	private TextField passwordTextField;
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	public void login(ActionEvent e) {
		
		String user = nicknameTextField.getText();
		String password = passwordTextField.getText();
		
		try {
			ServerConnection.login(user,password);
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
	}	

	public void switchToRegister(ActionEvent event) throws IOException {
		
		Parent root = FXMLLoader.load(getClass().getResource("Register.fxml"));

		stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		
	}
}
