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

    private String loggedInUser;

    private int currentTask = -1;


    @Subscribe
    public void msgListEvent(MessagesListEvent event) {
        List<String> msgs = List.of(event.getMessage().getData().split("\\|"));
        lst.getItems().clear();
        lst.getItems().addAll(msgs);
        System.out.println(event.getMessage());
        System.out.println(msgs);
    }

    @Subscribe
    public void givenTaskEvent(GivenTaskEvent event) {
        txtBox.setText(event.getMessage().getData());
    }

    @Subscribe
    public void taskMessageEvent(TaskMessageEvent event) {
        List<String> tasks = List.of(event.getMessage().getData().split("\\."));
        lst.getItems().clear();
        lst.getItems().addAll(tasks);
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
        System.out.println("SHOW TASKS CLICKED");
    }

    @FXML
    void onMessages(ActionEvent event) {
        System.out.println("MESSAGES CLICKED");
        sendMessage("get messages " + loggedInUser);
    }

    @FXML
    void showTask(ActionEvent event) {
        if (currentTask != -1) {
            sendMessage("give task " + currentTask);
        } else {
            txtBox.setText("please select a task");
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
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create new task");
        dialog.setHeaderText("Enter info about the task");
        dialog.setContentText("Info : ");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent((info) -> {
            sendMessage("new task " + loggedInUser + " " + info);
        });
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
}
