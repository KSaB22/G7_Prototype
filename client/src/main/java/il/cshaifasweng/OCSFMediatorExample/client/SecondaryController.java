/**
 * Sample Skeleton for 'secondary.fxml' Controller Class
 */

package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.greenrobot.eventbus.Subscribe;

public class SecondaryController {

    @FXML // fx:id="idTF"
    private TextField idTF; // Value injected by FXMLLoader

    @FXML // fx:id="loginBTN"
    private Button loginBTN; // Value injected by FXMLLoader

    @FXML // fx:id="passwordTF"
    private PasswordField passwordTF; // Value injected by FXMLLoader

    @FXML // fx:id="stressBTN"
    private Button stressBTN; // Value injected by FXMLLoader

    @Subscribe
    public void errorEvent(ErrorEvent event){
        Platform.runLater(() -> {
            Alert alert;
            if (event.getMessage().getMessage().equals("no user with that id")) {
                alert = new Alert(Alert.AlertType.ERROR, "A user with this ID does not exist");
            } else/* if (event.getMessage().getMessage().equals("wrong password"))*/{
                alert = new Alert(Alert.AlertType.ERROR, "Wrong password");
            }
            alert.setTitle("Error!");
            alert.setHeaderText("Error:");
            alert.show();
        });
    }

}
