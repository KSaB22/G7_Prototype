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
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.DoubleAccumulator;

public class PrimaryController {

    @FXML // fx:id="lst"
    private ListView<String> lst; // Value injected by FXMLLoader

    @FXML // fx:id="finishBtn"
    private Button finishBtn; // Value injected by FXMLLoader

    @FXML // fx:id="volunteerBtn"
    private Button volunteerBtn; // Value injected by FXMLLoader

    @FXML // fx:id="showBtn"
    private Button showBtn; // Value injected by FXMLLoader

    @FXML // fx:id="txtBox"
    private TextArea txtBox; // Value injected by FXMLLoader

    @FXML // fx:id="stressBTN"
    private Button stressBTN; // Value injected by FXMLLoader

    @FXML // fx:id="taskBtn"
    private Button taskBtn; // Value injected by FXMLLoader

    @FXML // fx:id="msgsBtn"
    private Button msgsBtn; // Value injected by FXMLLoader

    @FXML // fx:id="showTasksBtn"
    private Button showTasksBtn; // Value injected by FXMLLoader

    @FXML // fx:id="defaultDropdown"
    private ChoiceBox<String> defaultDropdown; // Value injected by FXMLLoader
    private String loggedInUser;

    private int currentTask = -1;
    private ArrayList<String> options = new ArrayList<>();

    ListShown listShown;

    public enum ListShown {
        TASKS,
        MESSAGES
    }


    @Subscribe
    public void msgListEvent(MessagesListEvent event) {
        Platform.runLater(() -> {
            List<String> msgs = List.of(event.getMessage().getData().split("\\|"));
            resetLst();
            lst.getItems().addAll(msgs);
            // System.out.println(event.getMessage());
            System.out.println("Received messages");
        });
    }

    @Subscribe
    public void givenTaskEvent(GivenTaskEvent event) {
        Platform.runLater(() -> {
            txtBox.setText(event.getMessage().getData());
        });
    }

    @Subscribe
    public void taskMessageEvent(TaskMessageEvent event) {
        Platform.runLater(() -> {
            List<String> tasks = List.of(event.getMessage().getData().split("\\."));
            resetLst();
            lst.getItems().addAll(tasks);
        });
    }

    @Subscribe
    public void errorEvent(ErrorEvent event) {
        Platform.runLater(() -> {
            Alert alert = null;
            if (event.getMessage().getMessage().equals("emergency prompt")) {
                alert = new Alert(Alert.AlertType.INFORMATION, event.getMessage().getData());
                alert.setTitle("Emergency recorded");
                alert.setHeaderText("Emergency");
            } else if (event.getMessage().getMessage().equals("already vol")) {
                alert = new Alert(Alert.AlertType.ERROR, "This task is already being worked on");
                alert.setTitle("Error!");
                alert.setHeaderText("Error:");
            } else if (event.getMessage().getMessage().equals("request rejected")) {
                alert = new Alert(Alert.AlertType.INFORMATION, event.getMessage().getData());
                alert.setTitle("Task rejected");
                alert.setHeaderText("Your task has been rejected");
            } else if (event.getMessage().getMessage().equals("creator cannot be volunteer")) {
                alert = new Alert(Alert.AlertType.ERROR, "You are the creator of this task. You cannot volunteer to do it.");
                alert.setTitle("Error!");
                alert.setHeaderText("Error:");
            }

            if (alert != null) {
                alert.show();
            }

        });
    }

    @Subscribe
    public void checkingEvent(CheckingEvent event) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = new Dialog();
                dialog.setTitle("Did you finish this task?");
                dialog.setHeaderText(event.getMessage().getData());
                dialog.getDialogPane().getButtonTypes().add(ButtonType.YES);
                dialog.getDialogPane().getButtonTypes().add(ButtonType.NO);
                Optional<ButtonType> result = dialog.showAndWait();
                if (result.isPresent()) {
                    if (result.get() == ButtonType.YES) {
                        sendMessage("finish " + event.getMessage().getMessage().split(" ")[3] + " " + loggedInUser + " prompt");
                    } else if (result.get() == ButtonType.NO) {
                        dialog.close();
                    }
                }
            }
        });
    }

    @FXML
    void onShowTasks(ActionEvent event) {
        // System.out.println("SHOW TASKS CLICKED");
        sendMessage("pull tasks");
        listShown = ListShown.TASKS;
    }

    @FXML
    void onMessages(ActionEvent event) {
        // System.out.println("MESSAGES CLICKED");
        sendMessage("get messages " + loggedInUser);
        listShown = ListShown.MESSAGES;
    }



    @FXML
    void showSelected(ActionEvent event) {
        switch (listShown){
            case TASKS -> showTask();
            case MESSAGES -> showMsgDescription();
        }
    }



    @FXML
    void onVolunteer(ActionEvent event) {
        if (currentTask != -1) {
            sendMessage("volunteer in " + currentTask + " " + loggedInUser);
        } else {
            txtBox.setText("please select a task");
        }
    }

	@FXML
	void onNewTask(ActionEvent event) {
		TextInputDialog dialog  = new TextInputDialog();
        if(defaultDropdown.getValue() == null || defaultDropdown.getValue().equals("None")){
            dialog.setTitle("Create new task");
            dialog.setHeaderText("Enter info about the task");
            dialog.setContentText("Info : ");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent((info) ->{
                sendMessage("new task " + loggedInUser + " " + info);
            });
        } else {
            sendMessage("new task " + loggedInUser + " " + defaultDropdown.getValue());
        }

	}

    @FXML
    void onEMG(ActionEvent event) {
        sendMessage("emergency " + loggedInUser);
    }

    @FXML
    void onEndRequest(ActionEvent event) {
        if (currentTask != -1) {
            sendMessage("finish " + currentTask + " " + loggedInUser);
        } else {
            txtBox.setText("please select a task");
        }
    }


    @FXML
    public void initialize() {
        EventBus.getDefault().register(this);

        options.add("None");
        options.add("Dog sit my dog");
        options.add("Paint my house");
        defaultDropdown.getItems().addAll(options);

        sendMessage("pull tasks");
        listShown = ListShown.TASKS;
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
            e.printStackTrace();
        }
    }

    void initData(String username) {
        loggedInUser = username;
    }

    @FXML
    void sendMessage(String messagetype) {
        try {
            Message message = new Message(SecondaryController.msgId++, messagetype);
            SimpleClient.getClient().sendToServer(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // HELPER-FUNCTIONS

    void showTask() {
        if (currentTask != -1) {
            sendMessage("give task " + currentTask);
        } else {
            txtBox.setText("please select a task");
        }
    }

    void showMsgDescription() {
        if (currentTask != -1) {
            String msg = lst.getSelectionModel().getSelectedItem();
            // System.out.println("SELECTED MESSAGE IS: " + msg);
            int index = msg.indexOf("Description:");
            // If "Description:" is found
            if (index != -1) {
                txtBox.setText(msg);
            } else {
                txtBox.setText("Message description not found");
            }
        } else {
            txtBox.setText("please select a message");
        }
    }

    void resetLst() {
        currentTask = -1;
        lst.getItems().clear();
    }


}
