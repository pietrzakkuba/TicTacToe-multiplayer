import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable {

    public Button button1, button2, button3, button4, button5, button6, button7, button8, button9, exitButton;
    public Label LabelYou, LabelOpponent, whoseTurn;
    public GridPane mainGrid;
    public Pane pane;
    private boolean isX, game_finished = false, draw = false;
    private String[] values = new String[9];


    private boolean myTurn() throws IOException {
        String turn = Main.readFromServer();
        if (turn.equals("11")) {
            Platform.runLater ( () -> {
                whoseTurn.setText("Your turn!");
                whoseTurn.setTextFill(Color.web("#106310"));
            });
            return true;
        }
        else if (turn.equals("12")) {
            Platform.runLater ( () -> {
                whoseTurn.setText("Opponent's turn!");
                whoseTurn.setTextFill(Color.web("#7F1010"));
            });
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
            game_finished = true;
        } else if
        (
           values[3].equals(values[4]) &&
            values[3].equals(values[5]) &&
            !values[3].equals("")
        ) {
            System.out.println("GAME OVER 456");
            game_finished = true;
        } else if
        (
            values[6].equals(values[7]) &&
            values[6].equals(values[8]) &&
            !values[6].equals("")
        ) {
            System.out.println("GAME OVER 789");
            game_finished = true;
        } else if
        (
            values[0].equals(values[3]) &&
            values[0].equals(values[6]) &&
            !values[0].equals("")
        ) {
            System.out.println("GAME OVER 147");
            game_finished = true;
        } else if
        (
            values[1].equals(values[4]) &&
            values[1].equals(values[7]) &&
            !values[1].equals("")
        ) {
            System.out.println("GAME OVER 258");
            game_finished = true;
        } else if
        (
            values[2].equals(values[5]) &&
            values[2].equals(values[8]) &&
            !values[2].equals("")
        ) {
            System.out.println("GAME OVER 369");
            game_finished = true;
        } else if
        (
            values[0].equals(values[4]) &&
            values[0].equals(values[8]) &&
            !values[0].equals("")
        ) {
            System.out.println("GAME OVER 159");
            game_finished = true;
        } else if
        (
            values[2].equals(values[4]) &&
            values[2].equals(values[6]) &&
            !values[2].equals("")
        ) {
            System.out.println("GAME OVER 357");
            game_finished = true;
        } else {
            boolean not_draw_flag = false;
            for (int i = 0; i < 9; i++) {
                if(values[i].equals("")) {
                    not_draw_flag = true;
                    break;
                }
            }
            if (!not_draw_flag) {
                System.out.println("DRAW");
                game_finished = true;
                draw = true;
            }
        }

    }

    private void changeAbility(boolean my_turn) {
        mainGrid.setDisable(!my_turn);
    }

    private void waitForMove() throws IOException {
        Button button;
        String message_from_server = Main.readFromServer();
        game_finished = message_from_server.charAt(0) == '2';
        char button_number = message_from_server.charAt(1);
        String button_id = "button" + button_number;
        values[button_number - '0' - 1] = isX ? "O" : "X";
        checkState();
        button = (Button)pane.getScene().lookup("#" + button_id);
        Platform.runLater ( () -> {
            button.setDisable(true);
            button.setText(isX ? "O" : "X");
            button.getStyleClass().add("not_my_buttons");
        });
        if (!game_finished) changeAbility(myTurn());
        else {
            Main.writeToServer(message_from_server + '\0');
            if(!draw) {
                Platform.runLater(() -> {
                    whoseTurn.setText("Opponent has won!");
                });
            }
            else {
                Platform.runLater(() -> {
                    whoseTurn.setText("Draw!");
                    whoseTurn.setTextFill(Color.web("#1A3A70"));
                });
            }
        }
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
            Button button = (Button)actionEvent.getSource();
            Platform.runLater( () -> {
                button.setDisable(true);
                button.setText(isX ? "X" : "O");
                button.getStyleClass().add("my_buttons");
            });
            String message_to_server = button.getId();
            char button_number = message_to_server.charAt(message_to_server.length() - 1);
            values[button_number - '0' - 1] = isX ? "X" : "O";
            checkState();
            String first_number = !game_finished ? "0" : "2";
            message_to_server = first_number + button_number + '\0';
            try {
                Main.writeToServer(message_to_server);
                if (!game_finished) {
                    changeAbility(myTurn());
                    waitForMove();
                }
                else if (!draw){
                    Platform.runLater ( () -> {
                        whoseTurn.setText("You have won!");
                    });
                    changeAbility(false);
                }
                else {
                    Platform.runLater ( () -> {
                        whoseTurn.setText("Draw!");
                        whoseTurn.setTextFill(Color.web("#1A3A70"));
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void exit(ActionEvent actionEvent) {
        Main.window.close();
    }
}
