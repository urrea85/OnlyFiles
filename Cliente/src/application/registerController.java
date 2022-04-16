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

public class registerController {
	
	@FXML
	private TextField nicknameTextField;
	
	@FXML
	private TextField passwordTextField;
	
	@FXML
	private TextField confirmPasswordTextField;
	
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	public void register(ActionEvent e) {
		
	}

	public void switchToLogin(ActionEvent event) throws IOException {
		
		Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));

		stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		
	}
	
}
