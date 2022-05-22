package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

public class metaViewController implements Initializable{
	
	@FXML
	private ListView<String> metaInfo;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		String[] metaEntries = Data.metaString.split("->");
		
		for(String file: metaEntries) {
			metaInfo.getItems().addAll(file);
		}
		
	}

}
