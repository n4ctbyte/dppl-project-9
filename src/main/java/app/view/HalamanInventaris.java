package app.view;

import app.SiurApp;
import app.model.Barang;
import app.model.Peminjaman;
import app.model.User;
import app.service.AuthService;
import app.service.InventarisService;
import app.service.PeminjamanService;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class HalamanInventaris {

    private SiurApp mainApp;
    private InventarisService inventarisService;
    private AuthService authService;
    private PeminjamanService peminjamanService;
    private FlowPane itemsContainer;
    private TextField searchField;
    
    public HalamanInventaris(SiurApp mainApp) {
        this.mainApp = mainApp;
        this.inventarisService = InventarisService.getInstance();
        this.authService = AuthService.getInstance();
        this.peminjamanService = PeminjamanService.getInstance();
    }

    public Node getView() {
        VBox contentContainer = new VBox(20);
        contentContainer.setPadding(new Insets(15));

        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.getStyleClass().add("header-bar");
        
        Label title = new Label("Daftar Inventaris");
        title.getStyleClass().add("label-title");
        HBox.setMargin(title, new Insets(0, 15, 0, 0));

        HBox searchBox = createSearchBox();

        Button kembalikanButton = new Button("Kembalikan Barang");
        kembalikanButton.getStyleClass().add("btn-kembali");
        kembalikanButton.setOnAction(e -> showReturnDialog());
        setupButtonHoverEffect(kembalikanButton, 1.05);
        
        topBar.getChildren().addAll(title, searchBox, kembalikanButton);
        
        User user = authService.getUserAktif();
        Label welcomeLabel = new Label("Selamat datang, " + user.getNama() + "!");
        welcomeLabel.getStyleClass().add("label-welcome");
        VBox.setMargin(welcomeLabel, new Insets(5, 0, 0, 5));
        
        itemsContainer = new FlowPane(20, 20);
        itemsContainer.setAlignment(Pos.TOP_LEFT);
        
        ScrollPane scrollPane = new ScrollPane(itemsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setPadding(new Insets(20, 5, 5, 5));

        contentContainer.getChildren().addAll(topBar, welcomeLabel, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        refreshInventoryList();
        
        return contentContainer;
    }
    
    private void setupButtonScaleAnimation(Node node, double scale) {
        ScaleTransition stIn = new ScaleTransition(Duration.millis(150), node);
        stIn.setToX(scale);
        stIn.setToY(scale);
        
        ScaleTransition stOut = new ScaleTransition(Duration.millis(150), node);
        stOut.setToX(1.0);
        stOut.setToY(1.0);
        
        node.setOnMouseEntered(e -> stIn.playFromStart());
        node.setOnMouseExited(e -> stOut.playFromStart());
    }
    
    private void setupButtonHoverEffect(Node node, double scale) {
        ScaleTransition stIn = new ScaleTransition(Duration.millis(150), node);
        stIn.setToX(scale);
        stIn.setToY(scale);
        
        ScaleTransition stOut = new ScaleTransition(Duration.millis(150), node);
        stOut.setToX(1.0);
        stOut.setToY(1.0);
        
        node.setOnMouseEntered(e -> stIn.playFromStart());
        node.setOnMouseExited(e -> stOut.playFromStart());
    }

    private HBox createSearchBox() {
        HBox searchContainer = new HBox();
        searchContainer.getStyleClass().add("text-field-search");
        searchContainer.setMaxWidth(340);
        searchContainer.setPrefHeight(45);

        searchField = new TextField();
        searchField.setPromptText("Cari Barang");
        searchField.setStyle("-fx-background-color: transparent; -fx-font-size: 13px; -fx-border-width: 0;");
        searchField.setPrefWidth(270);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterInventory(newVal);
        });

        Label searchIcon = new Label("🔍");
        searchIcon.setStyle("-fx-cursor: hand; -fx-font-size: 18px;");

        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchContainer.getChildren().addAll(searchField, searchIcon);

        return searchContainer;
    }

    private Node createImageNode(Barang barang) {
        String imgPath = barang.getPathGambar();
        Node imageNode;

        if (imgPath != null && !imgPath.isEmpty()) {
            try (InputStream is = getClass().getResourceAsStream("/" + imgPath)) {
                if (is == null) {
                    imageNode = createPlaceholder();
                } else {
                    Image img = new Image(is);
                    ImageView imageView = new ImageView(img);
                    imageView.setFitWidth(80);
                    imageView.setFitHeight(80);
                    imageView.setPreserveRatio(true);
                    imageNode = imageView;
                }
            } catch (Exception e) {
                imageNode = createPlaceholder();
            }
        } else {
            imageNode = createPlaceholder();
        }
        
        return imageNode;
    }

    private Node createPlaceholder() {
        Rectangle placeholder = new Rectangle(80, 80);
        placeholder.setFill(Color.web("#EEEEEE"));
        placeholder.setArcWidth(12);
        placeholder.setArcHeight(12);
        return placeholder;
    }
    
    private HBox createStockBox(Barang barang) {
        HBox stockBox = new HBox();
        stockBox.setPadding(new Insets(6, 12, 6, 12));
        
        String stockColor;
        if (barang.getStok() > 15) {
            stockColor = "#D4EDDA";
        } else if (barang.getStok() > 5) {
            stockColor = "#FFF3CD";
        } else {
            stockColor = "#F8D7DA";
        }

        stockBox.setStyle("-fx-background-color: " + stockColor + "; -fx-background-radius: 8;");
        stockBox.setMaxWidth(120);

        Label stockLabel = new Label("Stok : " + barang.getStok());
        stockLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        stockLabel.setTextFill(Color.BLACK);
        stockBox.getChildren().add(stockLabel);
        
        return stockBox;
    }

    private VBox createItemCard(Barang barang) {
        VBox cardWrapper = new VBox();
        
        HBox card = new HBox(15);
        card.getStyleClass().add("card-item");
        card.setMinWidth(380);
        card.setMaxWidth(380);
        card.setMinHeight(100);
        card.setAlignment(Pos.CENTER_LEFT);

        setupButtonScaleAnimation(card, 1.03);

        Node imageNode = createImageNode(barang);

        VBox infoBox = new VBox(8);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        
        Label nameLabel = new Label(barang.getNama());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        nameLabel.setWrapText(true);
        nameLabel.setTextFill(Color.BLACK);
        nameLabel.setMaxWidth(180);
        
        HBox stockBox = createStockBox(barang);

        Region vSpacer = new Region();
        VBox.setVgrow(vSpacer, Priority.ALWAYS);

        Button actionButton = new Button();
        actionButton.setPrefSize(100, 35);
        setupButtonScaleAnimation(actionButton, 1.08);
        
        actionButton.setText("Pinjam");
        actionButton.getStyleClass().add("btn-pinjam");
        
        actionButton.setOnAction(e -> showBorrowForm(barang));
        
        VBox.setMargin(actionButton, new Insets(5, 0, 0, 0));
        
        infoBox.getChildren().addAll(nameLabel, stockBox, vSpacer, actionButton);

        card.getChildren().addAll(imageNode, infoBox);
        cardWrapper.getChildren().add(card);

        return cardWrapper;
    }

    private void refreshInventoryList() {
        itemsContainer.getChildren().clear();
        List<Barang> items = inventarisService.getAllBarang();
        for (Barang item : items) {
            itemsContainer.getChildren().add(createItemCard(item));
        }
    }

    private void filterInventory(String keyword) {
        itemsContainer.getChildren().clear();
        List<Barang> filteredItems;
        if (keyword == null || keyword.trim().isEmpty()) {
            filteredItems = inventarisService.getAllBarang();
        } else {
            filteredItems = inventarisService.filterBarang(keyword);
        }
        
        for (Barang item : filteredItems) {
            itemsContainer.getChildren().add(createItemCard(item));
        }
    }

    private void showBorrowForm(Barang barang) {
        if (barang.getStok() <= 0) {
            showAlert(Alert.AlertType.WARNING, "Stok Habis", "Stok " + barang.getNama() + " saat ini habis.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        String judul = "Form Peminjaman Barang";
        dialog.setTitle(judul);

        ButtonType btnTipeAction = new ButtonType("Pinjam", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnTipeAction, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        User user = authService.getUserAktif();

        TextField namaPeminjam = new TextField(user.getNama());
        namaPeminjam.setDisable(true);
        TextField emailPeminjam = new TextField(user.getEmail());
        emailPeminjam.setDisable(true);
        TextField namaBarang = new TextField(barang.getNama());
        namaBarang.setDisable(true);
        
        TextField jumlahField = new TextField();
        jumlahField.setPromptText("Tulis jumlah (Maks: " + barang.getStok() + ")");
        
        TextArea alasan = new TextArea();
        String promptAlasan = "Alasan peminjaman...";
        alasan.setPromptText(promptAlasan);

        grid.add(new Label("Nama Peminjam:"), 0, 0);
        grid.add(namaPeminjam, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailPeminjam, 1, 1);
        grid.add(new Label("Nama Barang:"), 0, 2);
        grid.add(namaBarang, 1, 2);
        grid.add(new Label("Jumlah:"), 0, 3);
        grid.add(jumlahField, 1, 3);
        grid.add(new Label("Keterangan:"), 0, 4);
        grid.add(alasan, 1, 4);

        dialog.getDialogPane().setContent(grid);
        
        Button actionButton = (Button) dialog.getDialogPane().lookupButton(btnTipeAction);
        actionButton.setDefaultButton(true);

        actionButton.addEventFilter(ActionEvent.ACTION, event -> {
            String jumlahText = jumlahField.getText();
            String alasanPeminjaman = alasan.getText();
            int jumlah;

            try {
                jumlah = Integer.parseInt(jumlahText);
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Jumlah harus berupa angka.");
                event.consume();
                return;
            }

            if (jumlah <= 0) {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Jumlah harus lebih dari 0.");
                event.consume();
                return;
            }

            if (jumlah > barang.getStok()) {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Jumlah melebihi stok (Stok: " + barang.getStok() + ").");
                event.consume();
                return;
            }
            
            if (alasanPeminjaman == null || alasanPeminjaman.trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Keterangan wajib diisi.");
                event.consume();
                return;
            }

            boolean success = inventarisService.pinjamBarang(barang, jumlah);

            if (success) {
                peminjamanService.catatPeminjaman(user, barang, jumlah, alasanPeminjaman);
                String pesanSukses = "Berhasil meminjam ";
                showAlert(Alert.AlertType.INFORMATION, "Sukses", pesanSukses + jumlah + " unit " + barang.getNama() + "!");
                filterInventory(searchField.getText());
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Stok tidak mencukupi untuk jumlah yang diminta.");
                event.consume();
            }
        });

        dialog.showAndWait();
    }

    private void showReturnDialog() {
        User user = authService.getUserAktif();
        List<Peminjaman> pinjamanAktif = peminjamanService.getPeminjamanAktifByUser(user);

        if (pinjamanAktif.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Info", "Anda tidak memiliki barang yang sedang dipinjam.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Form Pengembalian Barang");

        ButtonType btnTipeKembalikan = new ButtonType("Kembalikan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnTipeKembalikan, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField namaPeminjam = new TextField(user.getNama());
        namaPeminjam.setDisable(true);
        TextField emailPeminjam = new TextField(user.getEmail());
        emailPeminjam.setDisable(true);

        ComboBox<Peminjaman> barangComboBox = new ComboBox<>();
        barangComboBox.getItems().addAll(pinjamanAktif);
        barangComboBox.setPromptText("Pilih barang yang dikembalikan");

        TextField jumlahField = new TextField();
        jumlahField.setPromptText("Jumlah yang dikembalikan");
        
        TextArea catatan = new TextArea();
        catatan.setPromptText("Catatan (Misal: Rusak, Hilang, dll)");
        
        CheckBox checkHabisPakai = new CheckBox("Barang habis pakai / Tidak kembali ke stok");

        barangComboBox.setOnAction(e -> {
            Peminjaman selected = barangComboBox.getValue();
            if (selected != null) {
                jumlahField.setPromptText("Jumlah (Maks: " + selected.getJumlahSisa() + ")");
            }
        });

        grid.add(new Label("Nama Peminjam:"), 0, 0);
        grid.add(namaPeminjam, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailPeminjam, 1, 1);
        grid.add(new Label("Nama Barang:"), 0, 2);
        grid.add(barangComboBox, 1, 2);
        grid.add(new Label("Jumlah:"), 0, 3);
        grid.add(jumlahField, 1, 3);
        grid.add(new Label("Catatan:"), 0, 4);
        grid.add(catatan, 1, 4);
        grid.add(checkHabisPakai, 1, 5);

        dialog.getDialogPane().setContent(grid);
        
        Button kembalikanButton = (Button) dialog.getDialogPane().lookupButton(btnTipeKembalikan);
        kembalikanButton.setDefaultButton(true);

        kembalikanButton.addEventFilter(ActionEvent.ACTION, event -> {
            Peminjaman peminjamanDipilih = barangComboBox.getValue();
            if (peminjamanDipilih == null) {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Anda harus memilih barang.");
                event.consume();
                return;
            }

            String jumlahText = jumlahField.getText();
            String catatanPengembalian = catatan.getText();
            int jumlah;

            try {
                jumlah = Integer.parseInt(jumlahText);
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Jumlah harus berupa angka.");
                event.consume();
                return;
            }

            if (catatanPengembalian == null || catatanPengembalian.trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Catatan wajib diisi.");
                event.consume();
                return;
            }
            
            if (jumlah > peminjamanDipilih.getJumlahSisa() || jumlah <= 0) {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Jumlah pengembalian tidak valid.");
                event.consume();
                return;
            }
            
            boolean isHabis = checkHabisPakai.isSelected();
            
            boolean success = peminjamanService.kembalikanPeminjaman(peminjamanDipilih, jumlah, catatanPengembalian, isHabis);

            if (success) {
                if (!isHabis) {
                    inventarisService.kembalikanBarang(peminjamanDipilih.getBarang(), jumlah);
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Berhasil mengembalikan " + jumlah + " unit " + peminjamanDipilih.getBarang().getNama() + "!");
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Barang dicatat sebagai habis pakai/hilang. Stok tidak bertambah.");
                }
                filterInventory(searchField.getText());
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Error saat memproses pengembalian.");
                event.consume();
            }
        });

        dialog.showAndWait();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}