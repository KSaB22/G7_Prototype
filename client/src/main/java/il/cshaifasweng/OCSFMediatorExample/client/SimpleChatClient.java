package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * JavaFX App
 */
public class SimpleChatClient extends Application {

    private static Scene scene;
    private SimpleClient client;
    private static Stage appStage;

    @Override
    public void start(Stage stage) throws IOException {
        client = SimpleClient.getClient();
    	client.openConnection();
        scene = new Scene(loadFXML("secondary"), 640, 480);
        //this.stage.setTitle("Login page");
        stage.setResizable(false);
        stage.setScene(scene);
        appStage = stage;
        stage.show();

    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SimpleChatClient.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void setContent(String pageName, String data) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SimpleChatClient.class.getResource(pageName + ".fxml"));
        Parent root = fxmlLoader.load();
        scene = new Scene(root);
        appStage.setScene(scene);
        PrimaryController pc = fxmlLoader.getController();
        pc.initData(data);
        appStage.show();
    }

    public static void switchScreen(String name, String data){
        Platform.runLater(() ->
        {
            try {
                setContent(name, data);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
	public void stop() throws Exception {
		super.stop();
	}




	public static void main(String[] args) {
        launch();
    }

}