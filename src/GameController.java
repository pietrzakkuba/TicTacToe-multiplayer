import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class GameController implements Initializable {

    public Button button1, button2, button3, button4, button5, button6, button7, button8, button9;
    public Label LabelYou, LabelOpponent;
    public GridPane mainGrid;
    public TextArea Console;
    public Pane pane;
    private boolean isX;
    private String[] values = new String[9];


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
        System.out.println("Something went wrong! " + turn);
        return false;
    }

    private void checkState() {

        if
        (
            values[0].equals(values[1]) &&
            values[0].equals(values[2]) &&
            !values[0].equals("")
        ) {
            System.out.println("GAME OVER 123");
        } else if
        (
           values[3].equals(values[4]) &&
            values[3].equals(values[5]) &&
            !values[3].equals("")
        ) {
            System.out.println("GAME OVER 456");
        } else if
        (
            values[6].equals(values[7]) &&
            values[6].equals(values[8]) &&
            !values[6].equals("")
        ) {
            System.out.println("GAME OVER 789");
        } else if
        (
            values[0].equals(values[3]) &&
            values[0].equals(values[6]) &&
            !values[0].equals("")
        ) {
            System.out.println("GAME OVER 147");
        } else if
        (
            values[1].equals(values[4]) &&
            values[1].equals(values[7]) &&
            !values[1].equals("")
        ) {
            System.out.println("GAME OVER 258");
        } else if
        (
            values[2].equals(values[5]) &&
            values[2].equals(values[8]) &&
            !values[2].equals("")
        ) {
            System.out.println("GAME OVER 369");
        } else if
        (
            values[0].equals(values[4]) &&
            values[0].equals(values[8]) &&
            !values[0].equals("")
        ) {
            System.out.println("GAME OVER 159");
        } else if
        (
            values[2].equals(values[4]) &&
            values[2].equals(values[6]) &&
            !values[2].equals("")
        ) {
            System.out.println("GAME OVER 357");
        }

    }

    private void changeAbility(boolean my_turn) {
        mainGrid.setDisable(!my_turn);
    }

    private void waitForMove() throws IOException {
        Button button;
        String message_from_server = Main.readFromServer();
        char button_number = message_from_server.charAt(1);
        String button_id = "button" + button_number;
        values[button_number - '0' - 1] = isX ? "O" : "X";
        checkState();
        button = (Button)pane.getScene().lookup("#" + button_id);
        Platform.runLater ( () -> {
            button.setDisable(true);
            button.setText(isX ? "O" : "X");
        });
        Console.appendText("\nI am receiving this from server: " + message_from_server);
        changeAbility(myTurn());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        new Thread( ()->{
            try {
                if (myTurn()) {
                    LabelYou.setText("X");
                    LabelOpponent.setText("O");
                    isX = true;
                } else {
                    LabelYou.setText("O");
                    LabelOpponent.setText("X");
                    changeAbility(false);
                    isX = false;
                    waitForMove();

                };
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread( ()->{
           for(int i=0; i<9; i++){
               values[i] = "";
           }
        }).start();
    }


    public void makeMove(ActionEvent actionEvent) throws IOException {

        new Thread(()->{
            String first_number = "0";
            Button button = (Button)actionEvent.getSource();
            Platform.runLater( () -> {
                button.setDisable(true);
                button.setText(isX ? "X" : "O");
            });
            String message_to_server = button.getId();
            char button_number = message_to_server.charAt(message_to_server.length() - 1);
            values[button_number - '0' - 1] = isX ? "X" : "O";
            checkState();
            message_to_server = first_number + button_number + '\0';
            if (!Console.getText().equals("")) Console.appendText("\n");
                Console.appendText("I am sending this to server:  " + message_to_server);
            try {
                Main.writeToServer(message_to_server);
                changeAbility(myTurn());

                waitForMove();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
