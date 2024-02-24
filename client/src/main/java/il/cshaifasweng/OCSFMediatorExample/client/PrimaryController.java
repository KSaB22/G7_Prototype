package il.cshaifasweng.OCSFMediatorExample.client;

/**
 * Sample Skeleton for 'primary.fxml' Controller Class
 */

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.greenrobot.eventbus.Subscribe;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

public class PrimaryController {

	@FXML // fx:id="lst"
	private ListView<?> lst; // Value injected by FXMLLoader

	@FXML // fx:id="makeBtn"
	private Button makeBtn; // Value injected by FXMLLoader

	@FXML // fx:id="showBtn"
	private Button showBtn; // Value injected by FXMLLoader

	@FXML // fx:id="txtBox"
	private TextArea txtBox; // Value injected by FXMLLoader

	@Subscribe
	public void errorEvent(ErrorEvent event){
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.ERROR,
					String.format("Message:\nId: %d\nData: %s\nTimestamp: %s\n",
							event.getMessage().getId(),
							event.getMessage().getMessage(),
							event.getMessage().getTimeStamp().format(dtf))
			);
			alert.setTitle("Error!");
			alert.setHeaderText("Error:");
			alert.show();
		});
	}

	@Subscribe
	public void getStarterData(NewSubscriberEvent event) {
		try {
			Message message = new Message(msgId, "send Submitters IDs");
			SimpleClient.getClient().sendToServer(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@FXML
	void sendMessage(ActionEvent event) {
		try {
			Message message = new Message(msgId++, MessageTF.getText());
			MessageTF.clear();
			SimpleClient.getClient().sendToServer(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	void initialize() {


	}
}
