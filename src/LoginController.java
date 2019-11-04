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

    private Task getWaitLayout = new Task<Void>() {
        @Override
        public Void call() {
                Platform.runLater ( () -> {
                    try {
                            Main.window.setScene(new Scene(FXMLLoader.load(getClass().getResource("login-wait-layout.fxml"))));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            return null;
        }
    };

    private Task getGameLayout = new Task<Void>() {
        @Override
        public Void call() {
            Platform.runLater ( () -> {
                try {
                    Main.window.setTitle("TicTacToe - Game");
                    Main.window.setScene(new Scene(FXMLLoader.load(getClass().getResource("game-layout.fxml"))));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            return null;
        }
    };

    public void join(ActionEvent actionEvent) throws IOException {
        String adr = serverTextField.getText();
        if (adr.equals("")) {
            Main.connect("127.0.0.1");
        }
        else {
            Main.connect(adr);
        }

        new Thread(getWaitLayout).start();

        new Thread(()->{
            try {
                if(Main.readFromServer().equals("sr")) {
                    new Thread(getGameLayout).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }

    public void exit(ActionEvent actionEvent) {
        System.out.println("EXIT");
    }



}