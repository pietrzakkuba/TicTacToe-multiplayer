import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class LoginWaitController implements Initializable {
    public Button boredButton;
    public Label waitLabel;
    public Button exitButton;
    public Button relogin;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new Thread( ()->{
            Main.window.setOnCloseRequest(Event::consume);
        }).start();
    }

    public void clientIsBored(ActionEvent actionEvent) {
        new Thread(() -> {
            Random rand = new Random();
            int rand_num1 = rand.nextInt(0xffffff + 1);
            int rand_num4 = rand.nextInt(0xffffff + 1);
            Platform.runLater( () -> {
                Main.window.getScene().getRoot().setStyle("-fx-background-color: " + String.format("#%06x", rand_num1) + ";");
                waitLabel.setStyle("-fx-text-fill: " + String.format("#%06x", rand_num4) + ";");
            });
        }).start();
    }
}
