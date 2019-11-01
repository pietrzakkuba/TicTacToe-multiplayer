import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    public TextField server_text_field;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Hello World!");
    }

    public void join(ActionEvent actionEvent) throws IOException {
        System.out.println("JOIN");
        System.out.println(server_text_field.getText());
        
        Stage window = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        window.setScene(new Scene(FXMLLoader.load(getClass().getResource("game-layout.fxml"))));
        window.show();
    }

    public void exit(ActionEvent actionEvent) {
        System.out.println("EXIT");
    }
}
