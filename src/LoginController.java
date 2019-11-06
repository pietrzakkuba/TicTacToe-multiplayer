import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController{
    
    public TextField serverTextField;
    public Button joinButton, exitButton;
    static String adr;

    private void connect() throws IOException {
        adr = serverTextField.getText();
        if (adr.equals("")) {
            adr ="127.0.0.1";
        }
        Main.connect(adr);
        new Thread( () -> {
            Platform.runLater ( () -> {
                try {
                    Main.window.setScene(new Scene(FXMLLoader.load(getClass().getResource("login-wait-layout.fxml"))));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }).start();
        new Thread(() -> {
            try {
                String msg = Main.readFromServer();
                if (msg.equals("sr")) {
                    Platform.runLater ( () -> {
                        try {
                            Main.window.setTitle("TicTacToe - Game");
                            Main.window.setScene(new Scene(FXMLLoader.load(getClass().getResource("game-layout.fxml"))));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void join(ActionEvent actionEvent) throws IOException {
        connect();
    }

    public void exit(ActionEvent actionEvent) throws IOException {
        Main.window.close();
    }



}
