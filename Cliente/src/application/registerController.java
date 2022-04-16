package application;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.Main;

public class registerController {
	
	@FXML
	private TextField nicknameTextField;
	
	@FXML
	private TextField passwordTextField;
	
	@FXML
	private TextField confirmPasswordTextField;
	
	@FXML
	private Label errorLabel;
	
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	public Boolean nullFields(){
		
		boolean result = true;
		
		if(nicknameTextField.getText() == null || passwordTextField.getText() == null || confirmPasswordTextField.getText() == null) {
			result = false;
		}
		
		return result;
	}
	
	public Boolean confirmPassword() {
		boolean result = true;
		
		if(passwordTextField.getText() != confirmPasswordTextField.getText()) {
			result = false;
		}
		
		return result;
	}
	
	public void register(ActionEvent e) {
		
		if(nullFields()) {
			if(confirmPassword()) {
				
				String nick = nicknameTextField.getText();
				String password = passwordTextField.getText();
				
				//Conexion a server
			}
			else {
				errorLabel.setText("Passwords must be equal");
			}
		}
		else {
			errorLabel.setText("All fields must be completed");
		}
	}
	
	public void generatePassword(ActionEvent e) {
		
		Main passwGen = new Main();
		
		String securePass = passwGen.generatePassword(64, true, true, true, true);
		
		passwordTextField.setText(securePass);
		
		confirmPasswordTextField.setText(securePass);
		
	}

	public void switchToLogin(ActionEvent event) throws IOException {
		
		Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));

		stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		
	}
	
}
