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
import java.util.ArrayList;
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

    @FXML // fx:id="showBtn"
    private Button showBtn; // Value injected by FXMLLoader

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
    ArrayList<Integer> taskIds = null;

    // EVENTBUS EVENT HANDLERS
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
        resetLst();
        taskIds = event.getMessage().getLst();
        lst.getItems().addAll(tasks);
    }

    @Subscribe
    public void givenTaskEvent(GivenTaskEvent event) {
        txtBox.setText(event.getMessage().getData());
    }


    // JAVAFX EVENT HANDLERS

    @FXML
    void onAccept(ActionEvent event) {//todo : accept request

    }
    @FXML
    void onReject(ActionEvent event) {//todo : reject request

    }

    @FXML
    void onEMG(ActionEvent event) {
        sendMessage("emergency " + loggedInUser);
    }

    @FXML
    void onMessages(ActionEvent event) {//todo : show messages from users
        resetLst();
        disableRequestBtns();
    }


    @FXML
    void onRequests(ActionEvent event) {
        resetLst();
        enableRequestBtns();
        sendMessage("pull requests " + loggedInUser);
        // if(currentTask != -1){
        //     txtBox.setText("requests box is empty");
        // } else {
        //     txtBox.setText("please select a task");
        // }

    }

    @FXML
    void showTask(ActionEvent event) {
        System.out.println("taskIds list: " + taskIds);
        if (currentTask != -1) {
            if(taskIds != null) {
                sendMessage("get task by id", String.valueOf(taskIds.get(currentTask)));
            } else {
                txtBox.setText("Task list is empty");
            }
        } else {
            txtBox.setText("please select a task");
        }
    }




    @FXML
    void onSend(ActionEvent event) {//todo : send report when manger rejects a request

    }

    @FXML
    void onTaskList(ActionEvent event) {//show ongoing tasks
        resetLst();
        disableRequestBtns();
        sendMessage("pull tasks "+ loggedInUser);
    }

    @FXML
    void showEmgCall(ActionEvent event) {
        resetLst();
        disableRequestBtns();
        sendMessage("pull emergency");
    }
    @FXML
    void onUsers(ActionEvent event) {
        resetLst();
        disableRequestBtns();
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

    void sendMessage(String messagetype, String data) {
        try {
            Message message = new Message(SecondaryController.msgId++, messagetype, data);
            SimpleClient.getClient().sendToServer(message);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // HELPER-FUNCTIONS
    void resetLst() {
        currentTask = -1;
        taskIds = null;
        lst.getItems().clear();
    }

    void enableRequestBtns() {
        rejBtn.setVisible(true);
        accBtn.setVisible(true);
        rejBtn.setDisable(false);
        accBtn.setDisable(false);
    }
    void disableRequestBtns() {
        rejBtn.setVisible(false);
        accBtn.setVisible(false);
        rejBtn.setDisable(true);
        accBtn.setDisable(true);
    }

}
