package app.view;

import app.SiurApp;
import app.model.User;
import app.service.AuthService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class HalamanLogin {

    private SiurApp mainApp;
    private AuthService authService;
    private TextField emailField;
    private PasswordField passwordField;

    public HalamanLogin(SiurApp mainApp) {
        this.mainApp = mainApp;
        this.authService = AuthService.getInstance();
    }

    public Node getView() {
        VBox mainContainer = new VBox(15);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(30, 35, 30, 35));
        mainContainer.setStyle("-fx-background-color: #F0F0F0;");
        mainContainer.setMaxSize(380, 620);
        mainContainer.getStyleClass().add("login-container");

        Circle avatar = new Circle(60);
        avatar.setFill(Color.WHITE);
        avatar.setStroke(Color.LIGHTGRAY);
        avatar.setStrokeWidth(2);
        VBox.setMargin(avatar, new Insets(15, 0, 15, 0));

        Label titleLabel = new Label("Sistem Inventaris Universitas Riau");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(300);

        Label infoLabel = new Label("Silakan login menggunakan email dengan\ndomain *unri.ac.id!");
        infoLabel.setFont(Font.font("Arial", 11));
        infoLabel.setAlignment(Pos.CENTER);
        infoLabel.setStyle("-fx-text-alignment: center;");

        emailField = new TextField();
        emailField.setPromptText("Alamat Email");
        emailField.setMaxWidth(300);
        emailField.setPrefHeight(45);
        emailField.setStyle(
                "-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-font-size: 13px; -fx-padding: 10 15 10 15;");
        VBox.setMargin(emailField, new Insets(8, 0, 0, 0));

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(300);
        passwordField.setPrefHeight(45);
        passwordField.setStyle(
                "-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-font-size: 13px; -fx-padding: 10 15 10 15;");

        emailField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleLogin();
            }
        });

        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleLogin();
            }
        });

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
            showAlert(Alert.AlertType.INFORMATION, "Lupa Password", "Silakan hubungi administrator untuk reset password.");
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        rememberForgotBox.getChildren().addAll(rememberCheckBox, spacer, forgotPasswordLabel);

        Button loginButton = new Button("MASUK");
        loginButton.setMaxWidth(300);
        loginButton.setPrefHeight(45);
        loginButton.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        loginButton.setStyle(
                "-fx-background-color: #4A90E2; -fx-text-fill: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-cursor: hand;");
        loginButton.setOnMouseEntered(e -> loginButton.setStyle(
                "-fx-background-color: #357ABD; -fx-text-fill: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-cursor: hand;"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle(
                "-fx-background-color: #4A90E2; -fx-text-fill: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-cursor: hand;"));

        loginButton.setOnAction(e -> handleLogin());

        Label helpLabel = new Label("Butuh bantuan? Hubungi kami di sini!");
        helpLabel.setFont(Font.font("Arial", 12));
        helpLabel.setAlignment(Pos.CENTER);
        helpLabel.setStyle("-fx-cursor: hand;");
        helpLabel.setOnMouseClicked(e -> {
            showAlert(Alert.AlertType.INFORMATION, "Hubungi Kami", "Email: support@unri.ac.id\nTelepon: (0761) 123456");
        });
        VBox.setMargin(helpLabel, new Insets(8, 0, 0, 0));

        Label footerLabel = new Label("2024 Universitas Riau. All rights reserved.");
        footerLabel.setFont(Font.font("Arial", 10));
        footerLabel.setTextFill(Color.DARKGRAY);
        footerLabel.setAlignment(Pos.CENTER);

        mainContainer.getChildren().addAll(avatar, titleLabel, infoLabel, emailField, passwordField,
                rememberForgotBox, loginButton, helpLabel, footerLabel);

        VBox wrapper = new VBox(mainContainer);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setStyle("-fx-background-color: #DCDCDC;");
        return wrapper;
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Silakan masukkan alamat email!");
            return;
        }

        if (password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Silakan masukkan password!");
            return;
        }

        boolean loginSuccess = authService.login(email, password);

        if (loginSuccess) {
            mainApp.showInventarisPage();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Email atau password salah.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}