import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
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
    public Button relogin;
    private boolean isX, game_finished = false, draw = false, my_turn;

    // 11 from server -> my Turn, 12 from server -> not my Turn
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
        // println just in case...
        System.out.println("Something went wrong! " + turn);
        return false;
    }

    // enabling / disabling TicTacToe grid so players can make a move only in their turn
    private void changeAbility(boolean my_turn) {
        mainGrid.setDisable(!my_turn);
    }

    // if one player leaves without making any move, other one is automatically joined to a new game
    private void rejoin() throws IOException {
        game_finished = true;
        Main.closeConnection();
        Main.connect(LoginController.adr);
        Platform.runLater( () -> {
            try {
                Main.window.setScene(new Scene(FXMLLoader.load(getClass().getResource("login-wait-layout.fxml"))));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        new Thread(() -> {
            try {
                String msg = Main.readFromServer();
                if (msg.equals("sr")) {
                    Platform.runLater( () -> {
                        try {
                            Main.window.setScene(new Scene(FXMLLoader.load(getClass().getResource("game-layout.fxml"))));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }

    private void waitForMove() throws IOException {
        Button button;
        // opponent's move
        String message_from_server = Main.readFromServer();
        // le -> (opponent) left early -> rejoin to an other game
        if (message_from_server.equals("le")) {
            rejoin();
        // opponent left during match -> you win by walk-over
        } else if (message_from_server.equals("sl")) {
            game_finished = true;
            Platform.runLater( () -> {
                changeAbility(false);
                whoseTurn.setText("You have won by walkover!");
                whoseTurn.setTextFill(Color.web("#1A3A70"));
            });
        }
        else {
            // else -> if opponent didn't leave
            String checkState = Main.readFromServer();
            //XX or OO just won, game over
            if (checkState.equals("XX") || checkState.equals("OO")) {
                game_finished = true;
            }
            // draw, game over
            if (checkState.equals("dd")) {
                game_finished = true;
                draw = true;
            }
            // checking whether whole msg have arrived
            if (message_from_server.length() == 2) {
                //getting what move opponent did last turn
                char button_number = message_from_server.charAt(1);
                String button_id = "button" + button_number;
                button = (Button)pane.getScene().lookup("#" + button_id);
                // disabling and styling that button
                Platform.runLater ( () -> {
                    button.setDisable(true);
                    button.setText(isX ? "O" : "X");
                    button.getStyleClass().add("not_my_buttons");
                });
                // if not game over my turn next
                if (!game_finished) changeAbility(myTurn());
                //else manage who won
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
            // just in case...
            else {
                System.out.println("Something went wrong getting opponent's move!");
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new Thread( () -> {
            Main.window.setOnCloseRequest(e -> {
                e.consume();
                try {
                    close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }).start();
        // setting up who is who
        new Thread( ()->{
            try {
                if (myTurn()) {
                    Platform.runLater( () -> {
                        LabelYou.setText("X");
                        LabelOpponent.setText("O");
                    });

                    isX = true;
                } else {
                    Platform.runLater( () -> {
                        LabelYou.setText("O");
                        LabelOpponent.setText("X");
                        changeAbility(false);
                    });
                    isX = false;
                    waitForMove();

                };
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }


    public void makeMove(ActionEvent actionEvent) throws IOException {
        // style clicked button
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
            // send message to server what you have just clicked
            try {
                Main.writeToServer(message_to_server); // write your move to server
            // check state of game
                String checkState = Main.readFromServer(); // check current state of game

                //below here similar stuff as in waitForMove void
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

    private void close() throws IOException {
        if (my_turn || game_finished) {
            Main.window.close();
            Main.closeConnection();
        }
        else {
            Main.window.close();
            new Thread(()->{
                try {
                    Main.readFromServer(); // prevents closing connection too fast
                    Main.closeConnection(); // now you finally can safely close connection
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public void exit(ActionEvent actionEvent) throws IOException {
        close();
    }

    public void reLogin(ActionEvent actionEvent) throws IOException {
        if (my_turn || game_finished) {
            Main.closeConnection();
                Platform.runLater( () -> {
                    try {
                        Main.window.setScene(new Scene(FXMLLoader.load(getClass().getResource("login-layout.fxml"))));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        }
        else {
            new Thread(()->{
                try {
                    Main.readFromServer(); // prevents closing connection too fast 1/2
                    Main.readFromServer(); // prevents closing connection too fast 2/2
                    Main.closeConnection(); // now you finally can safely close connection
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            Platform.runLater( () -> {
                try {
                    Main.window.setScene(new Scene(FXMLLoader.load(getClass().getResource("login-layout.fxml"))));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
