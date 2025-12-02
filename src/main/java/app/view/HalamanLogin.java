package app.view;

import app.SiurApp;
import app.service.AuthService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.io.InputStream;

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
        mainContainer.setMaxSize(380, 620);
        
        mainContainer.getStyleClass().add("login-container");

        Circle avatar = new Circle(60);
        avatar.setStroke(Color.WHITE);
        avatar.setStrokeWidth(3);
    
        try {
            InputStream is = getClass().getResourceAsStream("/images/logo.png");
            if (is != null) {
                Image logoImage = new Image(is);
                avatar.setFill(new ImagePattern(logoImage));
            } else {
                System.err.println("Logo tidak ditemukan di src/main/resources/images/logo.png");
                avatar.setFill(Color.WHITE); 
            }
        } catch (Exception e) {
            e.printStackTrace();
            avatar.setFill(Color.WHITE);
        }
        
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
        VBox.setMargin(emailField, new Insets(8, 0, 0, 0));

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(300);
        passwordField.setPrefHeight(45);

        emailField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) handleLogin();
        });

        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) handleLogin();
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
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        rememberForgotBox.getChildren().addAll(rememberCheckBox, spacer, forgotPasswordLabel);

        Button loginButton = new Button("MASUK");
        loginButton.setMaxWidth(300);
        loginButton.setPrefHeight(45);
        loginButton.getStyleClass().add("btn-primary");
        loginButton.setOnAction(e -> handleLogin());

        Label helpLabel = new Label("Butuh bantuan? Hubungi kami di sini!");
        helpLabel.setFont(Font.font("Arial", 12));
        helpLabel.setUnderline(true);
        helpLabel.setStyle("-fx-cursor: hand;");
        VBox.setMargin(helpLabel, new Insets(8, 0, 0, 0));

        Label footerLabel = new Label("2024 Universitas Riau. All rights reserved.");
        footerLabel.setFont(Font.font("Arial", 10));
        footerLabel.setTextFill(Color.DARKGRAY);
        footerLabel.setAlignment(Pos.CENTER);

        mainContainer.getChildren().addAll(avatar, titleLabel, infoLabel, emailField, passwordField,
                rememberForgotBox, loginButton, helpLabel, footerLabel);

        VBox wrapper = new VBox(mainContainer);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.getStyleClass().add("login-wrapper");
        
        return wrapper;
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Email dan Password harus diisi!").show();
            return;
        }

        if (authService.login(email, password)) {
            mainApp.showInventarisPage();
        } else {
            new Alert(Alert.AlertType.ERROR, "Email atau password salah.").show();
        }
    }
}