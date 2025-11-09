package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class SiurApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        
        BorderPane root = new BorderPane();
        Label welcomeLabel = new Label("Selamat Datang di SIUR!");
        root.setCenter(welcomeLabel);

        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("SIUR - Sistem Inventaris Universitas Riau");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}