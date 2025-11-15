package app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Login extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Sistem Inventaris Universitas Riau");
        
        // Main container
        VBox mainContainer = new VBox(15);
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setPadding(new Insets(30, 35, 30, 35));
        mainContainer.setStyle("-fx-background-color: #DCDCDC;");
        
        // Avatar/Logo circle
        Circle avatar = new Circle(60);
        avatar.setFill(Color.WHITE);
        avatar.setStroke(Color.LIGHTGRAY);
        avatar.setStrokeWidth(2);
        VBox.setMargin(avatar, new Insets(15, 0, 15, 0));
        
        // Title
        Label titleLabel = new Label("Sistem Inventaris Universitas Riau");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(300);
        
        // Info text
        Label infoLabel = new Label("Silakan login menggunakan email dengan\ndomain *unri.ac.id!");
        infoLabel.setFont(Font.font("Arial", 11));
        infoLabel.setAlignment(Pos.CENTER);
        infoLabel.setStyle("-fx-text-alignment: center;");
        
        // Email field
        TextField emailField = new TextField();
        emailField.setPromptText("Alamat Email");
        emailField.setMaxWidth(300);
        emailField.setPrefHeight(45);
        emailField.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-radius: 10;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 10 15 10 15;"
        );
        VBox.setMargin(emailField, new Insets(8, 0, 0, 0));
        
        // Password field
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(300);
        passwordField.setPrefHeight(45);
        passwordField.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-radius: 10;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 10 15 10 15;"
        );
        
        // Remember me and forgot password container
        HBox rememberForgotBox = new HBox();
        rememberForgotBox.setAlignment(Pos.CENTER_LEFT);
        rememberForgotBox.setMaxWidth(300);
        rememberForgotBox.setSpacing(60);
        
        CheckBox rememberCheckBox = new CheckBox("Ingat Saya");
        rememberCheckBox.setFont(Font.font("Arial", 11));
        
        Label forgotPasswordLabel = new Label("Lupa Password");
        forgotPasswordLabel.setFont(Font.font("Arial", 11));
        forgotPasswordLabel.setTextFill(Color.web("#333333"));
        forgotPasswordLabel.setStyle("-fx-cursor: hand;");
        forgotPasswordLabel.setOnMouseEntered(e -> forgotPasswordLabel.setTextFill(Color.BLUE));
        forgotPasswordLabel.setOnMouseExited(e -> forgotPasswordLabel.setTextFill(Color.web("#333333")));
        forgotPasswordLabel.setOnMouseClicked(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Lupa Password");
            alert.setHeaderText(null);
            alert.setContentText("Silakan hubungi administrator untuk reset password.");
            alert.showAndWait();
        });
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        rememberForgotBox.getChildren().addAll(rememberCheckBox, spacer, forgotPasswordLabel);
        
        // Login button with blue color
        Button loginButton = new Button("MASUK");
        loginButton.setMaxWidth(300);
        loginButton.setPrefHeight(45);
        loginButton.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        loginButton.setStyle(
            "-fx-background-color: #4A90E2;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-radius: 10;" +
            "-fx-cursor: hand;"
        );
        loginButton.setOnMouseEntered(e -> loginButton.setStyle(
            "-fx-background-color: #357ABD;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-radius: 10;" +
            "-fx-cursor: hand;"
        ));
        loginButton.setOnMouseExited(e -> loginButton.setStyle(
            "-fx-background-color: #4A90E2;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-radius: 10;" +
            "-fx-cursor: hand;"
        ));
        
        loginButton.setOnAction(e -> handleLogin(emailField, passwordField, primaryStage));
        
        // Help label
        Label helpLabel = new Label("Butuh bantuan? Hubungi kami di sini!");
        helpLabel.setFont(Font.font("Arial", 12));
        helpLabel.setAlignment(Pos.CENTER);
        helpLabel.setStyle("-fx-cursor: hand;");
        helpLabel.setOnMouseClicked(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Hubungi Kami");
            alert.setHeaderText(null);
            alert.setContentText("Email: support@unri.ac.id\nTelepon: (0761) 123456");
            alert.showAndWait();
        });
        VBox.setMargin(helpLabel, new Insets(8, 0, 0, 0));
        
        // Footer
        Label footerLabel = new Label("2024 Universitas Riau. All rights reserved.");
        footerLabel.setFont(Font.font("Arial", 10));
        footerLabel.setTextFill(Color.DARKGRAY);
        footerLabel.setAlignment(Pos.CENTER);
        
        // Add all components
        mainContainer.getChildren().addAll(
            avatar,
            titleLabel,
            infoLabel,
            emailField,
            passwordField,
            rememberForgotBox,
            loginButton,
            helpLabel,
            footerLabel
        );
        
        // Create scene
        Scene scene = new Scene(mainContainer, 380, 620);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    private void handleLogin(TextField emailField, PasswordField passwordField, Stage stage) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        
        // Validasi input
        if (email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Silakan masukkan alamat email!");
            return;
        }
        
        if (password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Silakan masukkan password!");
            return;
        }
        
        // Validasi domain email
        if (!email.endsWith("@unri.ac.id")) {
            showAlert(Alert.AlertType.ERROR, "Error", "Email harus menggunakan domain @unri.ac.id!");
            return;
        }
        
        // Login sukses
        showAlert(Alert.AlertType.INFORMATION, "Sukses", "Login berhasil!\nSelamat datang, " + email);
        
        // Buka halaman inventaris
        stage.close();
        HalamanInventaris halamanInventaris = new HalamanInventaris();
        Stage newStage = new Stage();
        halamanInventaris.start(newStage);
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}