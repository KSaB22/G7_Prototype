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
import javafx.scene.control.*;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;
public class MangerPage {

    @FXML // fx:id="accBtn"
    private Button accBtn; // Value injected by FXMLLoader

    @FXML // fx:id="dateEmgBtn"
    private DatePicker dateEmgBtn; // Value injected by FXMLLoader

    @FXML // fx:id="lst"
    private ListView<String> lst; // Value injected by FXMLLoader

    @FXML // fx:id="showTaskBtn"
    private Button showTaskBtn; // Value injected by FXMLLoader

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
    private String rejectedUser;

    @Subscribe
    public void givenTaskEvent(GivenTaskEvent event) {
        txtBox.setText(event.getMessage().getData());
    }

    @Subscribe
    public void RejectEvent(RejectEvent event){rejectedUser = event.getMessage().getData();}

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
    void onAccept(ActionEvent event) {
        if (currentTask != -1) {
            sendMessage("accept task " + currentTask + " " +loggedInUser);
            accBtn.setVisible(false);
        } else {
            txtBox.setText("please select a task");
        }
    }

    @FXML
    void onDateEmg(ActionEvent event) {
        String date = dateEmgBtn.getValue().toString();
        sendMessage("pull emergency " + date);
        dateEmgBtn.setVisible(false);
    }

    @FXML
    void onEMG(ActionEvent event) {sendMessage("emergency " + loggedInUser);}

    @FXML
    void onShowTask(ActionEvent event) {
        if (currentTask != -1) {
            sendMessage("give task " + currentTask);
        } else {
            txtBox.setText("please select a task");
        }
    }

    @FXML
    void onReject(ActionEvent event) {
        if (currentTask != -1) {
            txtBox.setText("Please report back to user");
            txtBox.clear();
            sendMessage("reject task " + currentTask + " " +loggedInUser);
            rejBtn.setVisible(false);
        } else {
            txtBox.setText("please select a task");
        }

    }

    @FXML
    void onRequests(ActionEvent event) {
        rejBtn.setVisible(true);
        accBtn.setVisible(true);
        sendMessage("pull requests");
        txtBox.setText("please select a task");
    }

    @FXML
    void onSend(ActionEvent event) {
        String report = txtBox.getText();
        if(!(rejectedUser.isEmpty())){
            sendMessage("report " +rejectedUser + " "+report);
            rejectedUser = null;
        }


    }

    @FXML
    void onTaskList(ActionEvent event) {//show ongoing tasks
        sendMessage("pull tasks "+ loggedInUser);
    }

    @FXML
    void showEmgCall(ActionEvent event) {
        dateEmgBtn.setVisible(true);
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
