package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class alertController implements Initializable{
	
	@FXML
	private Label messageLabel;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		messageLabel.setText(Data.alertMsg);
		
	}

}
