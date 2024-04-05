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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
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


    @FXML // fx:id="rejBtn"
    private Button rejBtn; // Value injected by FXMLLoader

    @FXML // fx:id="reqBtn"
    private Button reqBtn; // Value injected by FXMLLoader

    @FXML // fx:id="communityBtn"
    private CheckBox communityBtn; // Value injected by FXMLLoader


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
        System.out.println("tasks recieved " + tasks);
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
    void onCommunity(ActionEvent event) {
        if (communityBtn.isSelected())
        {
            resetLst();
            disableRequestBtns();
            sendMessage("pull emergency " + loggedInUser);
        }
        communityBtn.setVisible(false);

    }

    @FXML
    void onAccept(ActionEvent event) {
        if(currentTask != -1) {
            sendMessage("accept task " + getSelectedTaskId());
        } else notifySelectATask();
    }
    @FXML
    void onReject(ActionEvent event) {//todo : reject request
        if(currentTask != -1) {
            showRejectDialog();
        } else notifySelectATask();
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
        communityBtn.setVisible(false);
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
                sendMessage("get task by id", getSelectedTaskId());
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
        communityBtn.setVisible(false);
        resetLst();
        disableRequestBtns();
        sendMessage("pull tasks "+ loggedInUser);
    }

    @FXML
    void showEmgCall(ActionEvent event) {
        communityBtn.setVisible(true);
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

    String getSelectedTaskId() {
        if(currentTask == -1)
            return "";
        return String.valueOf(taskIds.get(currentTask));
    }

    void notifySelectATask() {
        txtBox.setText("please select a task");
    }

    // REJECTION DIALOG
    void showRejectDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Rejection Form");
        dialog.setHeaderText("Why are you rejecting this request?");
        dialog.setContentText("Reason:");

        // Get the DialogPane associated with the dialog
        DialogPane dialogPane = dialog.getDialogPane();

        // Create a TextArea instead of a TextField
        TextArea textArea = new TextArea();
        textArea.setText("");
        textArea.setPromptText("Enter your reason here");

        // Add the TextArea to the content of the DialogPane
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);
        GridPane content = (GridPane) dialogPane.getContent();

        content.getChildren().clear(); // Remove all child nodes from the content GridPane
        content.add(textArea, 1, 1);

        // Create a custom "Send" button that replaces "OK"
        ButtonType customSendButtonType = new ButtonType("Send", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().set(0, customSendButtonType); // Replace the default "OK" button with the custom button

        // Set action for the custom "OK" button
        dialog.setResultConverter(buttonType -> {
            if (buttonType == customSendButtonType) {
                // Handle custom "Send" button action
                String enteredText = textArea.getText(); // Get the text from the TextArea
                // System.out.println("Custom Send button clicked");
                // System.out.println("Entered text: " + enteredText);
                return enteredText; // Return the entered text
            }
            return null;
        });

        // Show the dialog
        dialog.showAndWait().ifPresent(result -> {
            // Handle the result
            sendMessage("reject task " + getSelectedTaskId() + " " + loggedInUser, result);
        });
    }
}
