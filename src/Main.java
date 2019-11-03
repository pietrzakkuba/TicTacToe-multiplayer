import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Main extends Application {

    private static Socket clientSocket;
    private static InputStream input;
    private static OutputStream output;
    static Stage window;


    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("login-layout.fxml"))));
        primaryStage.setTitle("TicTacToe - Login");
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }


    static void connect(String adr) throws IOException {
        clientSocket = new Socket(adr, 1234);
        input = clientSocket.getInputStream();
        output = clientSocket.getOutputStream();
    }

    static String readFromServer() throws IOException {
        StringBuilder msg = new StringBuilder();
        int x;
        while ((x = input.read()) != 0) {
            msg.append((char) x);
        }
        return msg.toString();
    }

    static void writeToServer(String msg) throws IOException {
        output.write(msg.getBytes());
    }

    static void closeConnection() throws IOException {
        clientSocket.close();
    }



}
