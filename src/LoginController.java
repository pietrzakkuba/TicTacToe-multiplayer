import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable, Runnable {

    public TextField server_text_field;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void run() {

    }

    private void connect(String adr) throws IOException {
        Socket clientSocket = new Socket(adr, 1234);
        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            if (Main.readFromServer(reader).equals("sr"))
                System.out.println("Second player ready!");
    }

    public void join(ActionEvent actionEvent) throws IOException {

        connect(server_text_field.getText());



        Stage window = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        window.setScene(new Scene(FXMLLoader.load(getClass().getResource("game-layout.fxml"))));
        window.setTitle("TicTacToe - Game");
        window.show();
    }

    public void exit(ActionEvent actionEvent) {
        System.out.println("EXIT");
    }



}
