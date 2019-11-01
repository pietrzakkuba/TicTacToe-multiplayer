import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController{

    public TextField server_text_field;

    public void join(ActionEvent actionEvent) throws IOException {

        Main.connect(server_text_field.getText());
        if (Main.readFromServer().equals("sr"))
            System.out.println("Second player ready!");
        Stage window = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        window.setScene(new Scene(FXMLLoader.load(getClass().getResource("game-layout.fxml"))));
        window.setTitle("TicTacToe - Game");
        window.show();
    }

    public void exit(ActionEvent actionEvent) {
        System.out.println("EXIT");
    }



}
