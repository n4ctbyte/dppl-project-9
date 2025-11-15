package app;

import app.service.AuthService;
import app.view.HalamanInventaris;
import app.view.HalamanLogin;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class SiurApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private AuthService authService;

    private HalamanLogin halamanLogin;
    private HalamanInventaris halamanInventaris;
    
    private Node halamanRiwayat;
    private Node halamanKelola;
    private Node halamanStatistik;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("SIUR - Sistem Inventaris Universitas Riau");

        this.authService = AuthService.getInstance();

        this.halamanLogin = new HalamanLogin(this);
        this.halamanInventaris = new HalamanInventaris(this);
        
        this.halamanRiwayat = createPlaceholderPage("Halaman Riwayat");
        this.halamanKelola = createPlaceholderPage("Halaman Kelola Barang");
        this.halamanStatistik = createPlaceholderPage("Halaman Statistik");

        this.rootLayout = new BorderPane();

        showLoginPage();

        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private Node createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #2E3348;");
        sidebar.setMinWidth(220);

        Label title = new Label("SIUR");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.WHITE);
        VBox.setMargin(title, new Insets(0, 0, 20, 0));

        Button btnInventaris = createNavButton("Inventaris");
        Button btnRiwayat = createNavButton("Riwayat");
        Button btnKelola = createNavButton("Kelola Barang");
        Button btnStatistik = createNavButton("Statistik");
        
        btnInventaris.setOnAction(e -> showInventarisPage());
        btnRiwayat.setOnAction(e -> rootLayout.setCenter(halamanRiwayat));
        btnKelola.setOnAction(e -> rootLayout.setCenter(halamanKelola));
        btnStatistik.setOnAction(e -> rootLayout.setCenter(halamanStatistik));

        sidebar.getChildren().addAll(btnInventaris, btnRiwayat);

        if (authService.isAdmin()) {
            sidebar.getChildren().addAll(btnKelola, btnStatistik);
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnLogout = createNavButton("Logout");
        btnLogout.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10; -fx-background-radius: 8;");
        btnLogout.setOnAction(e -> {
            authService.logout();
            showLoginPage();
        });

        sidebar.getChildren().addAll(spacer, btnLogout);
        return sidebar;
    }
    
    private Button createNavButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #A9B1D6; -fx-font-size: 14px; -fx-padding: 10; -fx-background-radius: 8;"
        );
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: #414868; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10; -fx-background-radius: 8;"
        ));
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #A9B1D6; -fx-font-size: 14px; -fx-padding: 10; -fx-background-radius: 8;"
        ));
        return button;
    }

    public void showLoginPage() {
        rootLayout.setLeft(null);
        rootLayout.setCenter(halamanLogin.getView());
    }

    public void showInventarisPage() {
        rootLayout.setLeft(createSidebar());
        rootLayout.setCenter(halamanInventaris.getView());
    }

    private Node createPlaceholderPage(String title) {
        VBox placeholder = new VBox(new Label(title));
        placeholder.setAlignment(Pos.CENTER);
        placeholder.setStyle("-fx-background-color: #F0F0F0;");
        return placeholder;
    }

    public static void main(String[] args) {
        launch(args);
    }
}