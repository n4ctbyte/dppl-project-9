package app.view;

import app.SiurApp;
import app.model.Barang;
import app.model.Peminjaman;
import app.model.User;
import app.service.AuthService;
import app.service.InventarisService;
import app.service.PeminjamanService;
import java.util.List;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class HalamanInventaris {

    private SiurApp mainApp;
    private InventarisService inventarisService;
    private AuthService authService;
    private PeminjamanService peminjamanService;
    private FlowPane itemsContainer;
    private TextField searchField;
    
    private String styleCard = "-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 2, 4);";
    private String styleCardHover = "-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #4A90E2; -fx-border-width: 1.5; -fx-effect: dropshadow(gaussian, rgba(74,144,226,0.3), 12, 0, 0, 4); -fx-cursor: hand;";
    private String styleButtonPinjam = "-fx-background-color: #4A90E2; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13; -fx-background-radius: 8; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(74,144,226,0.4), 8, 0, 1, 2);";
    private String styleButtonKembali = "-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14; -fx-padding: 8 12; -fx-background-radius: 8; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 1, 2);";
    private String styleSearchBox = "-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 5 10 5 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 1, 2);";

    public HalamanInventaris(SiurApp mainApp) {
        this.mainApp = mainApp;
        this.inventarisService = InventarisService.getInstance();
        this.authService = AuthService.getInstance();
        this.peminjamanService = PeminjamanService.getInstance();
    }

    public Node getView() {
        VBox contentContainer = new VBox(20);
        contentContainer.setPadding(new Insets(25));
        contentContainer.setStyle("-fx-background-color: #fafafa;");

        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("Daftar Inventaris");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button kembalikanButton = new Button("Kembalikan Barang");
        kembalikanButton.setStyle(styleButtonKembali);

        kembalikanButton.setOnAction(e -> showReturnDialog());

        HBox searchBox = createSearchBox();
        topBar.getChildren().addAll(title, spacer, kembalikanButton, searchBox);
        
        itemsContainer = new FlowPane(20, 20);
        itemsContainer.setAlignment(Pos.TOP_LEFT);
        
        ScrollPane scrollPane = new ScrollPane(itemsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setPadding(new Insets(5));

        contentContainer.getChildren().addAll(topBar, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        refreshInventoryList();
        
        return contentContainer;
    }

    private HBox createSearchBox() {
        HBox searchContainer = new HBox();
        searchContainer.setAlignment(Pos.CENTER_LEFT);
        searchContainer.setMaxWidth(340);
        searchContainer.setPrefHeight(45);
        searchContainer.setStyle(styleSearchBox);

        searchField = new TextField();
        searchField.setPromptText("Cari Barang");
        searchField.setStyle("-fx-background-color: transparent; -fx-font-size: 13px; -fx-border-width: 0;");
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

    private VBox createItemCard(Barang barang) {
        VBox card = new VBox(8);
        card.setMinWidth(320);
        card.setMaxWidth(320);
        card.setMinHeight(100);
        card.setPadding(new Insets(15));
        card.setStyle(styleCard);

        card.setOnMouseEntered(e -> card.setStyle(styleCardHover));
        card.setOnMouseExited(e -> card.setStyle(styleCard));

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(barang.getNama());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        nameLabel.setWrapText(true);
        nameLabel.setTextFill(Color.BLACK);
        nameLabel.setMaxWidth(180);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button pinjamButton = new Button("Pinjam");
        pinjamButton.setPrefSize(100, 38);
        pinjamButton.setStyle(styleButtonPinjam);

        pinjamButton.setOnAction(e -> showBorrowForm(barang));

        topRow.getChildren().addAll(nameLabel, spacer, pinjamButton);
        topRow.setAlignment(Pos.TOP_LEFT);

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

        card.getChildren().addAll(topRow, stockBox);

        return card;
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
        dialog.setTitle("Form Peminjaman Barang");

        ButtonType btnTipePinjam = new ButtonType("Pinjam", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnTipePinjam, ButtonType.CANCEL);

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
        alasan.setPromptText("Alasan peminjaman...");

        grid.add(new Label("Nama Peminjam:"), 0, 0);
        grid.add(namaPeminjam, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailPeminjam, 1, 1);
        grid.add(new Label("Nama Barang:"), 0, 2);
        grid.add(namaBarang, 1, 2);
        grid.add(new Label("Jumlah:"), 0, 3);
        grid.add(jumlahField, 1, 3);
        grid.add(new Label("Alasan:"), 0, 4);
        grid.add(alasan, 1, 4);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == btnTipePinjam) {
            String jumlahText = jumlahField.getText();
            String alasanPeminjaman = alasan.getText();
            int jumlah;

            try {
                jumlah = Integer.parseInt(jumlahText);
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Jumlah harus berupa angka.");
                return;
            }

            if (jumlah <= 0) {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Jumlah peminjaman harus lebih dari 0.");
                return;
            }

            if (jumlah > barang.getStok()) {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Jumlah peminjaman melebihi stok (Stok: " + barang.getStok() + ").");
                return;
            }
            
            if (alasanPeminjaman == null || alasanPeminjaman.trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Alasan peminjaman wajib diisi.");
                return;
            }

            boolean success = inventarisService.pinjamBarang(barang, jumlah);

            if (success) {
                peminjamanService.catatPeminjaman(user, barang, jumlah, alasanPeminjaman);
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Berhasil meminjam " + jumlah + " unit " + barang.getNama() + "!");
                filterInventory(searchField.getText());
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Stok tidak mencukupi untuk jumlah yang diminta.");
            }
        }
    }

    private void showReturnDialog() {
        User user = authService.getUserAktif();
        List<Peminjaman> pinjamanAktif = peminjamanService.getPeminjamanAktifByUser(user);

        if (pinjamanAktif.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Tidak Ada Peminjaman", "Anda tidak memiliki barang yang sedang dipinjam.");
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

        barangComboBox.setOnAction(e -> {
            Peminjaman selected = barangComboBox.getValue();
            if (selected != null) {
                jumlahField.setPromptText("Jumlah (Maks: " + selected.getJumlah() + ")");
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

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == btnTipeKembalikan) {
            Peminjaman peminjamanDipilih = barangComboBox.getValue();
            if (peminjamanDipilih == null) {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Anda harus memilih barang.");
                return;
            }

            String jumlahText = jumlahField.getText();
            String catatanPengembalian = catatan.getText();
            int jumlah;

            try {
                jumlah = Integer.parseInt(jumlahText);
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Jumlah harus berupa angka.");
                return;
            }

            if (catatanPengembalian == null || catatanPengembalian.trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Catatan wajib diisi.");
                return;
            }
            
            if (jumlah > peminjamanDipilih.getJumlah() || jumlah <= 0) {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Jumlah pengembalian tidak valid.");
                return;
            }
            
            boolean success = peminjamanService.kembalikanPeminjaman(peminjamanDipilih, jumlah, catatanPengembalian);

            if (success) {
                inventarisService.kembalikanBarang(peminjamanDipilih.getBarang(), jumlah);
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Berhasil mengembalikan " + jumlah + " unit " + peminjamanDipilih.getBarang().getNama() + "!");
                filterInventory(searchField.getText());
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Jumlah pengembalian lebih besar dari yang dipinjam.");
            }
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