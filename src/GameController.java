import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable {

    public Button button1, button2, button3, button4, button5, button6, button7, button8, button9;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private boolean myTurn(BufferedReader reader) throws IOException {
        String turn = Main.readFromServer(reader);
        if (turn.equals("11")) {
            System.out.println("Your turn");
            return true;
        }
        else if (turn.equals("12")) {
            System.out.println("Opponent's turn");
            return false;
        }
        System.out.println("Something went wrong!");
        return false;
    }


    public void makeMove(ActionEvent actionEvent) {

        String first_number = "0";
        String message_to_server;
        message_to_server = ((Button)actionEvent.getSource()).getId();

//        if (game_finished)
//            first_number = "2";

        message_to_server = first_number + message_to_server.charAt(message_to_server.length() - 1) + '\n';
        System.out.print(message_to_server);

    }
    public void waitForMove() {

    }


}
