import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable {

    public Button button1, button2, button3, button4, button5, button6, button7, button8, button9;

    private boolean myTurn() throws IOException {
        String turn = Main.readFromServer();
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            if (myTurn()) {
                System.out.println("I am X");
            } else {
                System.out.println("I am O");
                Main.readFromServer();
            }



        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public void makeMove(ActionEvent actionEvent) throws IOException {

        String first_number = "0";
        String message_to_server;
        message_to_server = ((Button)actionEvent.getSource()).getId();

//        if (game_finished)
//            first_number = "2";

        message_to_server = first_number + message_to_server.charAt(message_to_server.length() - 1) + '\0';
        System.out.print(message_to_server);
        Main.writeToServer(message_to_server);
        myTurn();

        System.out.println(Main.readFromServer());

    }
    public void waitForMove() throws IOException {
        System.out.println(Main.readFromServer());
    }


}
