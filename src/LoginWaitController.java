import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.Random;

public class LoginWaitController {
    public Button boredButton;
    public Label waitLabel;
    public Button exitButton;


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

    public void exit(ActionEvent actionEvent) throws IOException {
        new Thread(() -> {
            try {

//                Main.readFromServer(); // simulate sr
                Main.readFromServer(); // simulate turn
                Main.closeConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        Main.window.close();
    }
}
