/**
 * Sample Skeleton for 'secondary.fxml' Controller Class
 */

package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

public class SecondaryController {

    @FXML // fx:id="idTF"
    private TextField idTF; // Value injected by FXMLLoader

    @FXML // fx:id="loginBTN"
    private Button loginBTN; // Value injected by FXMLLoader

    @FXML // fx:id="passwordTF"
    private PasswordField passwordTF; // Value injected by FXMLLoader

    @FXML // fx:id="stressBTN"
    private Button stressBTN; // Value injected by FXMLLoader

    public static int msgId = 0;
    @Subscribe
    public void errorEvent(ErrorEvent event){
        Platform.runLater(() -> {
            Alert alert = null;
            if(event.getMessage().getMessage().equals("emergency prompt")){
                alert = new Alert(Alert.AlertType.INFORMATION, event.getMessage().getData());
                alert.setTitle("Emergency recorded");
                alert.setHeaderText("Emergency");
            }
            else if (event.getMessage().getMessage().equals("no user with that id")) {
                alert = new Alert(Alert.AlertType.ERROR, "A user with this ID does not exist");
                alert.setTitle("Error!");
                alert.setHeaderText("Error:");
            } else if (event.getMessage().getMessage().equals("wrong password")){
                alert = new Alert(Alert.AlertType.ERROR, "Wrong password");
                alert.setTitle("Error!");
                alert.setHeaderText("Error:");
            }
            alert.show();
        });
    }

    @Subscribe
    public void loginEvent(LoginEvent event)  {
        SimpleChatClient.switchScreen("primary", idTF.getText());
    }

    @Subscribe
    public void mangerEvent(MangerEvent event)  {
        SimpleChatClient.switchScreen("MangerPage", idTF.getText());
    }

    @FXML
    void onEMG(ActionEvent event) {
        sendMessage("emergency");
    }

    @FXML
    void onLoginAttempt(ActionEvent event) {
        if(idTF.getText().isEmpty() || passwordTF.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR, "please fill all the boxes");
            alert.setTitle("Error!");
            alert.setHeaderText("Error:");
            alert.show();
        } else {
            sendMessage("login " + idTF.getText() + " " + passwordTF.getText());
        }
    }
    @FXML
    void sendMessage(String messagetype) {
        try {
            Message message = new Message(msgId++, messagetype);
            SimpleClient.getClient().sendToServer(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void initialize() {
        EventBus.getDefault().register(this);
    }
}
