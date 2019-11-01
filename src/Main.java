import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage window) throws Exception {

        window.setScene(new Scene(FXMLLoader.load(getClass().getResource("login-layout.fxml"))));
        window.setTitle("TicTacToe - Login");
        window.show();
    }
    public static void main(String[] args) {
        launch(args);
    }

    static String readFromServer(BufferedReader reader) throws IOException {
        StringBuilder msg = new StringBuilder();
        int x;
        while ((x = reader.read()) != 0) {
            msg.append((char) x);
        }
        return msg.toString();
    }

}
