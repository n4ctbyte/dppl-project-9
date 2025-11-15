package app;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class HalamanInventaris extends Application {
    
    private VBox itemsContainer;
    private TextField searchField;
    private List<InventoryItem> items;
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Sistem Inventaris Universitas Riau");
        
        // Initialize inventory items
        items = new ArrayList<>();
        items.add(new InventoryItem("Proyektor", 5));
        items.add(new InventoryItem("Spidol Hitam", 25));
        items.add(new InventoryItem("Spidol Merah", 20));
        items.add(new InventoryItem("Whiteboard", 8));
        items.add(new InventoryItem("Laptop Dell", 10));
        items.add(new InventoryItem("Mouse Wireless", 30));
        items.add(new InventoryItem("Keyboard Mechanical", 15));
        items.add(new InventoryItem("Printer Canon", 6));
        items.add(new InventoryItem("Scanner", 4));
        items.add(new InventoryItem("Kabel HDMI", 18));
        
        // Main container
        BorderPane mainContainer = new BorderPane();
        mainContainer.setStyle("-fx-background-color: #DCDCDC;");
        
        // Header
        VBox header = createHeader();
        mainContainer.setTop(header);
        
        // Content area with scroll
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #DCDCDC; -fx-background-color: #DCDCDC;");
        
        VBox contentContainer = new VBox(15);
        contentContainer.setPadding(new Insets(20, 20, 20, 20));
        contentContainer.setAlignment(Pos.TOP_CENTER);
        contentContainer.setStyle("-fx-background-color: #DCDCDC;");
        
        // Inventaris button
        Button inventarisButton = new Button("Inventaris");
        inventarisButton.setMaxWidth(340);
        inventarisButton.setPrefHeight(45);
        inventarisButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        inventarisButton.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
        );
        inventarisButton.setOnMouseEntered(e -> inventarisButton.setStyle(
            "-fx-background-color: #F0F0F0;" +
            "-fx-background-radius: 10;" +
            "-fx-border-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 3);" +
            "-fx-scale-x: 1.02;" +
            "-fx-scale-y: 1.02;"
        ));
        inventarisButton.setOnMouseExited(e -> inventarisButton.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
        ));
        
        // Search box
        HBox searchBox = createSearchBox();
        
        // Items container
        itemsContainer = new VBox(15);
        itemsContainer.setAlignment(Pos.TOP_CENTER);
        refreshInventoryList();
        
        contentContainer.getChildren().addAll(inventarisButton, searchBox, itemsContainer);
        scrollPane.setContent(contentContainer);
        
        mainContainer.setCenter(scrollPane);
        
        // Bottom navigation
        HBox bottomNav = createBottomNavigation(primaryStage);
        mainContainer.setBottom(bottomNav);
        
        // Create scene
        Scene scene = new Scene(mainContainer, 380, 620);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(20, 20, 20, 20));
        header.setStyle("-fx-background-color: #DCDCDC; -fx-border-color: black; -fx-border-width: 0 0 2 0;");
        
        // Avatar circle
        Circle avatar = new Circle(35);
        avatar.setFill(Color.WHITE);
        avatar.setStroke(Color.LIGHTGRAY);
        avatar.setStrokeWidth(2);
        
        // Title
        Label titleLabel = new Label("Sistem Inventaris\nUniversitas Riau");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setStyle("-fx-text-alignment: center;");
        
        HBox headerBox = new HBox(20);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(10, 20, 10, 20));
        headerBox.getChildren().addAll(avatar, titleLabel);
        
        header.getChildren().add(headerBox);
        header.setPrefHeight(120);
        
        return header;
    }
    
    private HBox createSearchBox() {
        HBox searchContainer = new HBox();
        searchContainer.setAlignment(Pos.CENTER_LEFT);
        searchContainer.setMaxWidth(340);
        searchContainer.setPrefHeight(45);
        searchContainer.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-radius: 10;" +
            "-fx-padding: 5 10 5 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
        );
        
        // Add hover effect to search box
        searchContainer.setOnMouseEntered(e -> searchContainer.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #4A90E2;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;" +
            "-fx-padding: 5 10 5 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(74,144,226,0.3), 8, 0, 0, 3);"
        ));
        searchContainer.setOnMouseExited(e -> {
            if (!searchField.isFocused()) {
                searchContainer.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-radius: 10;" +
                    "-fx-padding: 5 10 5 15;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
                );
            }
        });
        
        searchField = new TextField();
        searchField.setPromptText("Cari Barang");
        searchField.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-font-size: 13px;" +
            "-fx-border-width: 0;"
        );
        searchField.setPrefWidth(270);
        
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterInventory(newVal);
        });
        
        Label searchIcon = new Label("🔍");
        searchIcon.setFont(Font.font(18));
        searchIcon.setStyle("-fx-cursor: hand;");
        
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchContainer.getChildren().addAll(searchField, searchIcon);
        
        return searchContainer;
    }
    
    private VBox createItemCard(InventoryItem item) {
        VBox card = new VBox(8);
        card.setMaxWidth(340);
        card.setPrefHeight(100);
        card.setPadding(new Insets(15));
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-border-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
        );
        
        // Add hover effect to card
        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-border-radius: 15;" +
            "-fx-border-color: #4A90E2;" +
            "-fx-border-width: 2;" +
            "-fx-effect: dropshadow(gaussian, rgba(74,144,226,0.3), 10, 0, 0, 3);" +
            "-fx-cursor: hand;"
        ));
        card.setOnMouseExited(e -> card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-border-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
        ));
        
        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);
        
        // Item name
        Label nameLabel = new Label(item.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Pinjam button with blue color
        Button pinjamButton = new Button("Pinjam");
        pinjamButton.setPrefSize(100, 38);
        pinjamButton.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        pinjamButton.setStyle(
            "-fx-background-color: #4A90E2;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-border-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(74,144,226,0.3), 5, 0, 0, 2);"
        );
        pinjamButton.setOnMouseEntered(e -> pinjamButton.setStyle(
            "-fx-background-color: #357ABD;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-border-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(74,144,226,0.5), 8, 0, 0, 3);" +
            "-fx-scale-x: 1.05;" +
            "-fx-scale-y: 1.05;"
        ));
        pinjamButton.setOnMouseExited(e -> pinjamButton.setStyle(
            "-fx-background-color: #4A90E2;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-border-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(74,144,226,0.3), 5, 0, 0, 2);"
        ));
        
        pinjamButton.setOnAction(e -> handleBorrow(item));
        
        topRow.getChildren().addAll(nameLabel, spacer, pinjamButton);
        
        // Stock info with color indicator
        HBox stockBox = new HBox();
        stockBox.setPadding(new Insets(6, 12, 6, 12));
        
        // Change color based on stock level
        String stockColor;
        if (item.getStock() > 15) {
            stockColor = "#D4EDDA"; // Green - plenty
        } else if (item.getStock() > 5) {
            stockColor = "#FFF3CD"; // Yellow - medium
        } else {
            stockColor = "#F8D7DA"; // Red - low
        }
        
        stockBox.setStyle(
            "-fx-background-color: " + stockColor + ";" +
            "-fx-background-radius: 8;"
        );
        stockBox.setMaxWidth(120);
        
        Label stockLabel = new Label("Stok : " + item.getStock());
        stockLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        stockBox.getChildren().add(stockLabel);
        
        card.getChildren().addAll(topRow, stockBox);
        
        return card;
    }
    
    private void refreshInventoryList() {
        itemsContainer.getChildren().clear();
        for (InventoryItem item : items) {
            itemsContainer.getChildren().add(createItemCard(item));
        }
    }
    
    private void filterInventory(String keyword) {
        itemsContainer.getChildren().clear();
        if (keyword == null || keyword.trim().isEmpty()) {
            refreshInventoryList();
            return;
        }
        
        String lowerKeyword = keyword.toLowerCase();
        for (InventoryItem item : items) {
            if (item.getName().toLowerCase().contains(lowerKeyword)) {
                itemsContainer.getChildren().add(createItemCard(item));
            }
        }
    }
    
    private void handleBorrow(InventoryItem item) {
        if (item.getStock() > 0) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Konfirmasi");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Pinjam " + item.getName() + "?");
            
            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    item.decreaseStock();
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Sukses");
                    success.setHeaderText(null);
                    success.setContentText("Berhasil meminjam " + item.getName() + "!");
                    success.showAndWait();
                    refreshInventoryList();
                }
            });
        } else {
            Alert warning = new Alert(Alert.AlertType.WARNING);
            warning.setTitle("Peringatan");
            warning.setHeaderText(null);
            warning.setContentText("Stok " + item.getName() + " habis!");
            warning.showAndWait();
        }
    }
    
    private HBox createBottomNavigation(Stage stage) {
        HBox navBar = new HBox();
        navBar.setAlignment(Pos.CENTER);
        navBar.setPrefHeight(65);
        navBar.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #D3D3D3;" +
            "-fx-border-width: 2 0 0 0;"
        );
        
        Button homeButton = createNavButton("🏠", "Home", true);
        Button historyButton = createNavButton("🕐", "Riwayat", false);
        Button boxButton = createNavButton("📦", "Barang", false);
        Button logoutButton = createNavButton("➡️", "Keluar", false);
        
        logoutButton.setOnAction(e -> {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Konfirmasi Logout");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Yakin ingin keluar?");
            
            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    stage.close();
                    Login login = new Login();
                    Stage loginStage = new Stage();
                    login.start(loginStage);
                }
            });
        });
        
        HBox.setHgrow(homeButton, Priority.ALWAYS);
        HBox.setHgrow(historyButton, Priority.ALWAYS);
        HBox.setHgrow(boxButton, Priority.ALWAYS);
        HBox.setHgrow(logoutButton, Priority.ALWAYS);
        
        navBar.getChildren().addAll(homeButton, historyButton, boxButton, logoutButton);
        
        return navBar;
    }
    
    private Button createNavButton(String icon, String text, boolean active) {
        Button button = new Button(icon + "\n" + text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(65);
        button.setFont(Font.font("Arial", 16));
        button.setAlignment(Pos.CENTER);
        
        if (active) {
            // Active button with light blue background
            button.setStyle(
                "-fx-background-color: #E3F2FD;" +
                "-fx-text-fill: #1976D2;" +
                "-fx-border-width: 0;" +
                "-fx-cursor: hand;" +
                "-fx-text-alignment: center;"
            );
        } else {
            button.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-width: 0;" +
                "-fx-cursor: hand;" +
                "-fx-text-alignment: center;"
            );
            button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #F5F5F5;" +
                "-fx-border-width: 0;" +
                "-fx-cursor: hand;" +
                "-fx-text-alignment: center;"
            ));
            button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-width: 0;" +
                "-fx-cursor: hand;" +
                "-fx-text-alignment: center;"
            ));
        }
        
        return button;
    }
    
    // Inner class for inventory items
    class InventoryItem {
        private String name;
        private int stock;
        
        public InventoryItem(String name, int stock) {
            this.name = name;
            this.stock = stock;
        }
        
        public String getName() {
            return name;
        }
        
        public int getStock() {
            return stock;
        }
        
        public void decreaseStock() {
            if (stock > 0) {
                stock--;
            }
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}