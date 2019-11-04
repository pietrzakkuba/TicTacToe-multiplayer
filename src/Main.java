import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;

public class Main extends Application {

    private static Socket clientSocket;
    private static InputStream input;
    private static OutputStream output;
    private static Socket clientSocketSupport;
    private static InputStream inputSupport;
    private static OutputStream outputSupport;
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
        Random rand = new Random();
        char a = (char)(rand.nextInt(26) + 'a');
        char b = (char)(rand.nextInt(26) + 'a');
        String msg = String.valueOf(a) + b + '\0';
        clientSocket = new Socket(adr, 1234);
        input = clientSocket.getInputStream();
        output = clientSocket.getOutputStream();
        writeToServer(msg);
        clientSocketSupport = new Socket(adr, 1234);
        inputSupport = clientSocketSupport.getInputStream();
        outputSupport = clientSocketSupport.getOutputStream();
        writeToServerSupport(msg);
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

    static String readFromServerSupport() throws IOException {
        StringBuilder msg = new StringBuilder();
        int x;
        while ((x = inputSupport.read()) != 0) {
            msg.append((char) x);
        }
        return msg.toString();
    }

    static void writeToServerSupport(String msg) throws IOException {
        outputSupport.write(msg.getBytes());
    }

    static void closeConnection() throws IOException {
        clientSocket.close();
        clientSocketSupport.close();
    }



}
