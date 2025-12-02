package app;

import app.service.AuthService;
import app.view.HalamanInventaris;
import app.view.HalamanKelolaBarang;
import app.view.HalamanLogin;
import app.view.HalamanRiwayat;
import app.view.HalamanStatistik;
import app.model.User;
import java.io.InputStream;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
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
    
    private Button btnInventaris;
    private Button btnRiwayat;
    private Button btnKelola;
    private Button btnStatistik;

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
        
        showLoginPage();

        Scene scene = new Scene(rootLayout);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private Node createHeader() {
        HBox header = new HBox(15);
        header.getStyleClass().add("header-bar");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15));

        Label title = new Label("Sistem Inventaris Universitas Riau - SIUR");
        title.getStyleClass().add("label-title");
        title.setStyle("-fx-font-size: 20px;"); 

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String namaUser = "Guest";
        if (authService.getUserAktif() != null) {
            namaUser = "Halo, " + authService.getUserAktif().getNama();
        }
        Label lblUser = new Label(namaUser);
        lblUser.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333; -fx-font-size: 14px;");

        Button btnLogout = new Button("Logout");
        btnLogout.getStyleClass().add("btn-logout");
        btnLogout.setOnAction(e -> doLogout());

        header.getChildren().addAll(title, spacer, lblUser, btnLogout);
        return header;
    }

    private Node createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setMinWidth(240);
        sidebar.setMaxWidth(240);
        BorderPane.setMargin(sidebar, new Insets(15));

        VBox profileBox = new VBox(5);
        profileBox.setAlignment(Pos.CENTER);
        profileBox.setPadding(new Insets(0, 0, 10, 0));

        Circle avatar = new Circle(35);
        avatar.setStroke(Color.web("#4A90E2"));
        avatar.setStrokeWidth(2);
        
        User currentUser = authService.getUserAktif();
        String namaUser = "Guest";
        String roleUser = "";
        
        String imgPath = "images/profile.png"; 

        if (currentUser != null) {
            namaUser = currentUser.getNama();
            roleUser = currentUser.getRole();
            
            if (currentUser.getProfile() != null && !currentUser.getProfile().isEmpty()) {
                imgPath = currentUser.getProfile();
            }
        }

        try {
            InputStream is = getClass().getResourceAsStream("/" + imgPath);
            
            if (is != null) {
                avatar.setFill(new ImagePattern(new Image(is)));
            } else {
                InputStream defaultIs = getClass().getResourceAsStream("/images/profile.png");
                if (defaultIs != null) {
                    avatar.setFill(new ImagePattern(new Image(defaultIs)));
                } else {
                    avatar.setFill(Color.WHITE);
                }
            }
        } catch (Exception e) {
            avatar.setFill(Color.WHITE);
        }

        Label lblNama = new Label(namaUser);
        lblNama.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2E3348; -fx-wrap-text: true; -fx-text-alignment: center;");
        
        Label lblRole = new Label(roleUser);
        lblRole.setStyle("-fx-font-size: 12px; -fx-text-fill: #757575; -fx-background-color: #E0E0E0; -fx-padding: 2 8; -fx-background-radius: 10;");

        profileBox.getChildren().addAll(avatar, lblNama, lblRole);
        
        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 10, 0));

        btnInventaris = createNavButton("Inventaris");
        btnRiwayat = createNavButton("Riwayat");
        btnKelola = createNavButton("Kelola Barang");
        btnStatistik = createNavButton("Statistik");
        
        btnInventaris.setOnAction(e -> showInventarisPage());
        btnRiwayat.setOnAction(e -> showRiwayatPage());
        btnKelola.setOnAction(e -> {
            updateCenterContent(halamanKelolaBarang.getView());
            setActiveButton(btnKelola);
        });
        btnStatistik.setOnAction(e -> {
            updateCenterContent(halamanStatistik.getView());
            setActiveButton(btnStatistik);
        });

        sidebar.getChildren().addAll(profileBox, separator, btnInventaris, btnRiwayat);

        if (authService.isAdmin()) {
            sidebar.getChildren().addAll(btnKelola, btnStatistik);
        }

        return sidebar;
    }
    
    private Button createNavButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.getStyleClass().add("btn-sidebar");
        return button;
    }
    
    private void setActiveButton(Button activeButton) {
        resetButtonStyle(btnInventaris);
        resetButtonStyle(btnRiwayat);
        if (btnKelola != null) resetButtonStyle(btnKelola);
        if (btnStatistik != null) resetButtonStyle(btnStatistik);
        
        activeButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
    }
    
    private void resetButtonStyle(Button btn) {
        if (btn != null) {
            btn.setStyle("");
        }
    }

    private void updateCenterContent(Node content) {
        rootLayout.setTop(createHeader());
        rootLayout.setLeft(createSidebar());
        rootLayout.setCenter(content);
    }

    public void showLoginPage() {
        rootLayout.setTop(null);
        rootLayout.setLeft(null);
        rootLayout.setCenter(halamanLogin.getView());
    }
    
    public void doLogout() {
        authService.logout();
        showLoginPage();
    }

    public void showInventarisPage() {
        updateCenterContent(halamanInventaris.getView());
        setActiveButton(btnInventaris); 
    }
    
    public void showRiwayatPage() {
        halamanRiwayat.loadRiwayatData();
        updateCenterContent(halamanRiwayat.getView());
        setActiveButton(btnRiwayat);
    }

    public static void main(String[] args) {
        launch(args);
    }
}