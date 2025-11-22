package app;

import app.service.AuthService;
import app.view.HalamanInventaris;
import app.view.HalamanKelolaBarang;
import app.view.HalamanLogin;
import app.view.HalamanRiwayat;
import app.view.HalamanStatistik;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.application.Application;
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
    private HalamanRiwayat halamanRiwayat;
    private HalamanKelolaBarang halamanKelolaBarang;
    private HalamanStatistik halamanStatistik;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("SIUR - Sistem Inventaris Universitas Riau");

        this.authService = AuthService.getInstance();

        this.halamanLogin = new HalamanLogin(this);
        this.halamanInventaris = new HalamanInventaris(this);
        this.halamanRiwayat = new HalamanRiwayat(this);
        this.halamanKelolaBarang = new HalamanKelolaBarang(this);
        this.halamanStatistik = new HalamanStatistik(this);

        this.rootLayout = new BorderPane();
        this.rootLayout.setStyle("-fx-background-color: #f5f7fa;");

        showLoginPage();

        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private Node createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 2, 4);");
        sidebar.setMinWidth(240);
        sidebar.setMaxWidth(240);
        BorderPane.setMargin(sidebar, new Insets(15));

        Label title = new Label("SIUR");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#2E3348"));
        VBox.setMargin(title, new Insets(0, 0, 20, 0));

        Button btnInventaris = createNavButton("Inventaris");
        Button btnRiwayat = createNavButton("Riwayat");
        Button btnKelola = createNavButton("Kelola Barang");
        Button btnStatistik = createNavButton("Statistik");
        
        btnInventaris.setOnAction(e -> showInventarisPage());
        btnRiwayat.setOnAction(e -> showRiwayatPage());
        btnKelola.setOnAction(e -> rootLayout.setCenter(halamanKelolaBarang.getView()));
        btnStatistik.setOnAction(e -> rootLayout.setCenter(halamanStatistik.getView()));

        sidebar.getChildren().addAll(btnInventaris, btnRiwayat);

        if (authService.isAdmin()) {
            sidebar.getChildren().addAll(btnKelola, btnStatistik);
        }

        return sidebar;
    }
    
    private Button createNavButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        String styleIdle = "-fx-background-color: #F8F8F8; -fx-background-radius: 8; -fx-text-fill: #2E3348; -fx-font-size: 14px; -fx-padding: 10; -fx-alignment: center-left; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 1, 1);";
        String styleHover = "-fx-background-color: #E3F2FD; -fx-background-radius: 8; -fx-text-fill: #0B5ED7; -fx-font-size: 14px; -fx-padding: 10; -fx-alignment: center-left; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 1, 1); -fx-font-weight: bold;";
        button.setStyle(styleIdle);
        button.setOnMouseEntered(e -> button.setStyle(styleHover));
        button.setOnMouseExited(e -> button.setStyle(styleIdle));
        return button;
    }

    public void showLoginPage() {
        rootLayout.setLeft(null);
        rootLayout.setCenter(halamanLogin.getView());
    }
    
    public void doLogout() {
        authService.logout();
        showLoginPage();
    }

    public void showInventarisPage() {
        rootLayout.setLeft(createSidebar());
        rootLayout.setCenter(halamanInventaris.getView());
    }
    
    public void showRiwayatPage() {
        rootLayout.setLeft(createSidebar());
        halamanRiwayat.loadRiwayatData();
        rootLayout.setCenter(halamanRiwayat.getView());
    }

    public static void main(String[] args) {
        launch(args);
    }
}