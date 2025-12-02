package fhtw.quattuor.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        //FXMLLoader fxmlLoader = new FXMLLoader(fhtw.quattuor.client.ClientApplication.class.getResource("hello-view.fxml"));
        //Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        ClientConnectFourGrid grid = new ClientConnectFourGrid();
        VBox root = grid.generateGrid();
        Scene scene = new Scene(root, 600, 600);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}