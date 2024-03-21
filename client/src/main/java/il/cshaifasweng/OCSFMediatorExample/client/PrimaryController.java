/**
 * Sample Skeleton for 'primary.fxml' Controller Class
 */

package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.List;

public class PrimaryController{

	@FXML // fx:id="lst"
	private ListView<String> lst; // Value injected by FXMLLoader

	@FXML // fx:id="makeBtn"
	private Button makeBtn; // Value injected by FXMLLoader

	@FXML // fx:id="showBtn"
	private Button showBtn; // Value injected by FXMLLoader

	@FXML // fx:id="txtBox"
	private TextArea txtBox; // Value injected by FXMLLoader


	private int currentTask = -1;


	@Subscribe
	public void givenTaskEvent(GivenTaskEvent event){
		txtBox.setText(event.getMessage().getData());
	}

	@Subscribe
	public void taskMessageEvent(TaskMessageEvent event){
		List<String> tasks = List.of(event.getMessage().getData().split("\\."));
		lst.getItems().clear();
		lst.getItems().addAll(tasks);
	}

	@Subscribe
	public void errorEvent(ErrorEvent event){
		Platform.runLater(() -> {

			Alert alert = new Alert(Alert.AlertType.ERROR, "This task is already being worked on");
			alert.setTitle("Error!");
			alert.setHeaderText("Error:");
			alert.show();
		});
	}


	@FXML
	void showTask(ActionEvent event) {
		if(currentTask != -1){
			sendMessage("give task " + currentTask);
		} else {
			txtBox.setText("please select a task");
		}
	}
	@FXML
	void onVolunteer(ActionEvent event) {
		if(currentTask != -1){
			sendMessage("volunteer in " + currentTask);
		} else {
			txtBox.setText("please select a task");
		}
	}

	@FXML
	public void initialize() {
		EventBus.getDefault().register(this);
		sendMessage("pull tasks");
		lst.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
				currentTask = lst.getSelectionModel().getSelectedIndex();
			}
		});


		try {
			Message message = new Message(SecondaryController.msgId, "add client");
			SimpleClient.getClient().sendToServer(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	void sendMessage(String messagetype) {
		try {
			Message message = new Message(SecondaryController.msgId++, messagetype);
			SimpleClient.getClient().sendToServer(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
