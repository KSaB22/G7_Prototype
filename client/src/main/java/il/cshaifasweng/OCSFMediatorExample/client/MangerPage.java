/**
 * Sample Skeleton for 'MangerPage.fxml' Controller Class
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
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;
public class MangerPage {

    @FXML // fx:id="accBtn"
    private Button accBtn; // Value injected by FXMLLoader

    @FXML // fx:id="lst"
    private ListView<String> lst; // Value injected by FXMLLoader

    @FXML // fx:id="messagesBtn"
    private Button messagesBtn; // Value injected by FXMLLoader

    @FXML // fx:id="rejBtn"
    private Button rejBtn; // Value injected by FXMLLoader

    @FXML // fx:id="reqBtn"
    private Button reqBtn; // Value injected by FXMLLoader

    @FXML // fx:id="sendBtn"
    private Button sendBtn; // Value injected by FXMLLoader

    @FXML // fx:id="showEmgBtn"
    private Button showEmgBtn; // Value injected by FXMLLoader

    @FXML // fx:id="stressBTN"
    private Button stressBTN; // Value injected by FXMLLoader

    @FXML // fx:id="taskListBtn"
    private Button taskListBtn; // Value injected by FXMLLoader

    @FXML // fx:id="txtBox"
    private TextArea txtBox; // Value injected by FXMLLoader
    @FXML // fx:id="usersBtn"
    private Button usersBtn; // Value injected by FXMLLoader


    private String loggedInUser;
    private int currentTask = -1;

    @Subscribe
    public void errorEvent(ErrorEvent event){
        Platform.runLater(() -> {
            Alert alert;
            if(event.getMessage().getMessage().equals("emergency prompt")){
                alert = new Alert(Alert.AlertType.INFORMATION, event.getMessage().getData());
                alert.setTitle("Emergency recorded");
                alert.setHeaderText("Emergency");
            }
            else {//remember to delete
                alert = new Alert(Alert.AlertType.ERROR, "This task is already being worked on");
                alert.setTitle("Error!");
                alert.setHeaderText("Error:");
            }

            alert.show();
        });
    }

    @Subscribe
    public void taskMessageEvent(TaskMessageEvent event){
        List<String> tasks = List.of(event.getMessage().getData().split("\\."));
        lst.getItems().clear();
        lst.getItems().addAll(tasks);
    }


    @FXML
    void onAccept(ActionEvent event) {//todo : accept request

    }

    @FXML
    void onEMG(ActionEvent event) {
        sendMessage("emergency " + loggedInUser);
    }

    @FXML
    void onMessages(ActionEvent event) {//todo : show messages from users

    }

    @FXML
    void onReject(ActionEvent event) {//todo : reject request

    }

    @FXML
    void onRequests(ActionEvent event) {
        rejBtn.setVisible(true);
        accBtn.setVisible(true);
        sendMessage("pull requests");
        if(currentTask != -1){
            txtBox.setText("requests box is empty");
        } else {
            txtBox.setText("please select a task");
        }

    }

    @FXML
    void onSend(ActionEvent event) {//todo : send report when manger rejects a request

    }

    @FXML
    void onTaskList(ActionEvent event) {//show ongoing tasks
        sendMessage("pull tasks "+ loggedInUser);
    }

    @FXML
    void showEmgCall(ActionEvent event) {
        sendMessage("pull emergency");
    }
    @FXML
    void onUsers(ActionEvent event) {
        sendMessage("pull users " + loggedInUser);

    }



    @FXML
    public void initialize() {
        EventBus.getDefault().register(this);
        //sendMessage("pull tasks " + loggedInUser);
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

    void initData(String username){
        loggedInUser = username;
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
