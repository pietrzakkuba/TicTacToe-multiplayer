import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {



    @Override
    public void start(Stage window) throws Exception {

        Parent login_layout = FXMLLoader.load(getClass().getResource("login-layout.fxml"));
        window.setTitle("TicTacToe");
        window.setScene(new Scene(login_layout, 400, 200));
        window.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
