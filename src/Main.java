import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class Main extends Application {

    private static Socket clientSocket;
    private static InputStream input;
    private static OutputStream output;
    static Stage window;


    @Override
    public void start(Stage window2) throws Exception {
        window = window2;
        window2.setScene(new Scene(FXMLLoader.load(getClass().getResource("login-layout.fxml"))));
        window2.setTitle("TicTacToe - Login");
        window2.show();
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
