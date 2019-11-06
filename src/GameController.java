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
    private boolean isX, game_finished = false, draw = false, my_turn;


    private boolean myTurn() throws IOException {
        String turn = Main.readFromServer();
        if (turn.equals("11")) {
            my_turn = true;
            Platform.runLater ( () -> {
                whoseTurn.setText("Your turn!");
                whoseTurn.setTextFill(Color.web("#106310"));
            });
            return true;
        }
        else if (turn.equals("12")) {
            my_turn = false;
            Platform.runLater ( () -> {
                whoseTurn.setText("Opponent's turn!");
                whoseTurn.setTextFill(Color.web("#7F1010"));
            });
            return false;
        }
        System.out.println("Something went wrong! " + turn);
        return false;
    }


    private void changeAbility(boolean my_turn) {
        mainGrid.setDisable(!my_turn);
    }

    private void waitForMove() throws IOException {
        Button button;
        String message_from_server = Main.readFromServer();
        if (message_from_server.equals("sl")) {
            game_finished = true;
            Platform.runLater( () -> {
                changeAbility(false);
                whoseTurn.setText("You have won by walkover!");
                whoseTurn.setTextFill(Color.web("#1A3A70"));
            });
        }
        else {
            String checkState = Main.readFromServer();

            if (checkState.equals("XX") || checkState.equals("OO")) {
                game_finished = true;
            }
            if (checkState.equals("dd")) {
                game_finished = true;
                draw = true;
            }

            char button_number = message_from_server.charAt(1);
            String button_id = "button" + button_number;
            button = (Button)pane.getScene().lookup("#" + button_id);
            Platform.runLater ( () -> {
                button.setDisable(true);
                button.setText(isX ? "O" : "X");
                button.getStyleClass().add("not_my_buttons");
            });
            if (!game_finished) changeAbility(myTurn());
            else {
                if(!draw) {
                    Platform.runLater(() -> {
                        whoseTurn.setText("You have lost!");
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
            message_to_server = "0" + button_number + '\0';
            try {
                Main.writeToServer(message_to_server); //write your move to server

                String checkState = Main.readFromServer(); //check current state of game

                if (checkState.equals("XX") || checkState.equals("OO")) {
                    game_finished = true;
                }
                else if (checkState.equals("dd")) {
                    game_finished = true;
                    draw = true;
                }

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

    public void exit(ActionEvent actionEvent) throws IOException {
        if (my_turn || game_finished) {
            Main.window.close();
            Main.closeConnection();
        }
        else {
            Main.window.close();
            new Thread(()->{
                try {
                    Main.readFromServer(); // simulate waiting for opponent's move
                    Main.readFromServer(); // simulate waiting for new game state
                    Main.closeConnection(); // now you finally can safely close connection
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }

    }
}
