import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static java.lang.Thread.sleep;

public class LoginController{
    
    public TextField serverTextField;
    public Button joinButton;
    public Button exitButton;

    private Task getConnection = new Task<Void>() {
        @Override
        public Void call() {
            Platform.runLater ( () -> {
                try {
                    Main.connect(serverTextField.getText());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            return null;
        }
    };

    private Task getWaitLayout = new Task<Void>() {
        @Override
        public Void call() {
                Platform.runLater ( () -> {
                    try {
                        Main.window.setScene(new Scene(FXMLLoader.load(getClass().getResource("login-wait-layout.fxml"))));
                        Main.window.show();
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
                    Main.window.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            return null;
        }
    };

    public void join(ActionEvent actionEvent) throws IOException {

        Main.connect(serverTextField.getText());

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
