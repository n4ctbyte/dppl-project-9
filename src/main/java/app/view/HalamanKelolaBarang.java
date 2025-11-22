package app.view;

import app.SiurApp;
import app.model.Barang;
import app.service.InventarisService;
import java.io.File;
import java.util.Optional;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class HalamanKelolaBarang {

    private SiurApp mainApp;
    private InventarisService inventarisService;
    private TableView<Barang> tableView;
    private File selectedImageFile;

    public HalamanKelolaBarang(SiurApp mainApp) {
        this.mainApp = mainApp;
        this.inventarisService = InventarisService.getInstance();
    }

    public Node getView() {
        VBox contentContainer = new VBox(20);
        contentContainer.setPadding(new Insets(25));
        contentContainer.setStyle("-fx-background-color: #f5f7fa;");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("Kelola Barang");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: #2E3348;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button btnTambah = new Button("＋ Tambah Barang");
        
        String styleTambah = "-fx-background-color: #4A90E2; -fx-background-radius: 8; -fx-text-fill: #FFFFFF; -fx-font-size: 14px; -fx-padding: 10 20; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(74,144,226,0.4), 8, 0, 0, 2); -fx-cursor: hand; -fx-border-color: transparent; -fx-border-width: 1.5; -fx-border-radius: 8;";

        String styleTambahHover = "-fx-background-color: #FFFFFF; -fx-background-radius: 8; -fx-text-fill: #4A90E2; -fx-font-size: 14px; -fx-padding: 10 20; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(74,144,226,0.4), 8, 0, 0, 2); -fx-cursor: hand; -fx-border-color: #4A90E2; -fx-border-width: 1.5; -fx-border-radius: 8;";
        
        btnTambah.setStyle(styleTambah);
        
        setupButtonHoverEffect(btnTambah, 1.05, styleTambah, styleTambahHover);
        
        btnTambah.setOnAction(e -> showFormDialog(null));
        
        header.getChildren().addAll(title, spacer, btnTambah);

        setupTableView();
        refreshTable();

        contentContainer.getChildren().addAll(header, tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS);
        
        return contentContainer;
    }

    private void setupButtonHoverEffect(Node node, double scale, String styleIdle, String styleHover) {
        ScaleTransition stIn = new ScaleTransition(Duration.millis(150), node);
        stIn.setToX(scale);
        stIn.setToY(scale);
        
        ScaleTransition stOut = new ScaleTransition(Duration.millis(150), node);
        stOut.setToX(1.0);
        stOut.setToY(1.0);
        
        node.setOnMouseEntered(e -> {
            node.setStyle(styleHover);
            stIn.playFromStart();
        });
        node.setOnMouseExited(e -> {
            node.setStyle(styleIdle);
            stOut.playFromStart();
        });
    }

    private void setupButtonScaleOnly(Node node) {
        ScaleTransition stIn = new ScaleTransition(Duration.millis(150), node);
        stIn.setToX(1.15);
        stIn.setToY(1.15);
        
        ScaleTransition stOut = new ScaleTransition(Duration.millis(150), node);
        stOut.setToX(1.0);
        stOut.setToY(1.0);
        
        node.setOnMouseEntered(e -> stIn.playFromStart());
        node.setOnMouseExited(e -> stOut.playFromStart());
    }

    private void setupTableView() {
        tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 1);");

        TableColumn<Barang, String> colKode = new TableColumn<>("Kode");
        colKode.setCellValueFactory(new PropertyValueFactory<>("kode"));
        colKode.setStyle("-fx-alignment: CENTER-LEFT;");

        TableColumn<Barang, String> colNama = new TableColumn<>("Nama Barang");
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colNama.setStyle("-fx-alignment: CENTER-LEFT;");

        TableColumn<Barang, Integer> colStok = new TableColumn<>("Stok");
        colStok.setCellValueFactory(new PropertyValueFactory<>("stok"));
        colStok.setStyle("-fx-alignment: CENTER;");

        TableColumn<Barang, Void> colAksi = new TableColumn<>("Aksi");
        colAksi.setStyle("-fx-alignment: CENTER;");
        
        colAksi.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("✎");
            private final Button btnHapus = new Button("🗑");
            private final HBox pane = new HBox(10, btnEdit, btnHapus);

            {
                pane.setAlignment(Pos.CENTER);
                
                String styleEdit = "-fx-background-color: #FFC107; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 50%; -fx-min-width: 35px; -fx-min-height: 35px; -fx-max-width: 35px; -fx-max-height: 35px; -fx-cursor: hand;";
                
                String styleHapus = "-fx-background-color: #FF5252; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 50%; -fx-min-width: 35px; -fx-min-height: 35px; -fx-max-width: 35px; -fx-max-height: 35px; -fx-cursor: hand;";

                btnEdit.setStyle(styleEdit);
                btnEdit.setTooltip(new Tooltip("Edit Data"));
                setupButtonScaleOnly(btnEdit);
                
                btnHapus.setStyle(styleHapus);
                btnHapus.setTooltip(new Tooltip("Hapus Barang"));
                setupButtonScaleOnly(btnHapus);
                
                btnEdit.setOnAction(event -> showFormDialog(getTableView().getItems().get(getIndex())));
                btnHapus.setOnAction(event -> handleDelete(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        tableView.getColumns().addAll(colKode, colNama, colStok, colAksi);
    }

    private void refreshTable() {
        tableView.setItems(FXCollections.observableArrayList(inventarisService.getAllBarang()));
    }

    private void handleDelete(Barang barang) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Konfirmasi Hapus");
        dialog.setHeaderText("Menghapus: " + barang.getNama());
        dialog.setContentText("Masukkan alasan penghapusan (Wajib):");

        // Ambil tombol OK dari dialog
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        
        // Tambahkan filter agar tidak menutup jika kosong
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            String alasan = dialog.getEditor().getText();
            if (alasan == null || alasan.trim().isEmpty()) {
                event.consume(); // Batalkan penutupan dialog
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Gagal");
                alert.setHeaderText(null);
                alert.setContentText("Alasan penghapusan harus diisi!");
                alert.show();
            } else {
                // Logika hapus dijalankan di sini sebelum dialog tutup
                inventarisService.hapusBarang(barang, alasan, "ADMIN");
                refreshTable();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Sukses");
                alert.setHeaderText(null);
                alert.setContentText("Barang berhasil dihapus.");
                alert.show();
            }
        });

        dialog.showAndWait();
    }

    private void showFormDialog(Barang barangEdit) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(barangEdit == null ? "Tambah Barang Baru" : "Edit Data Barang");

        ButtonType btnSaveType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSaveType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 50, 10, 10));

        // Komponen Input
        ComboBox<String> comboLokasi = new ComboBox<>();
        comboLokasi.getItems().addAll("FT", "FEB", "FKIP", "FK", "FISIP", "FMIPA", "RKT");
        
        ComboBox<String> comboKategori = new ComboBox<>();
        comboKategori.getItems().addAll("ELK", "ATK");
        
        TextField kodePreview = new TextField();
        kodePreview.setEditable(false);
        kodePreview.setStyle("-fx-background-color: #E0E0E0;");
        
        TextField nama = new TextField(barangEdit != null ? barangEdit.getNama() : "");
        TextField stok = new TextField(barangEdit != null ? String.valueOf(barangEdit.getStok()) : "");
        
        Label pathLabel = new Label(barangEdit != null ? barangEdit.getPathGambar() : "Belum ada gambar");
        Button btnUpload = new Button("Upload Gambar");
        selectedImageFile = null; 

        // Logika Auto-Generate Kode
        if (barangEdit == null) {
            comboLokasi.setOnAction(e -> updateKodePreview(comboLokasi, comboKategori, kodePreview));
            comboKategori.setOnAction(e -> updateKodePreview(comboLokasi, comboKategori, kodePreview));
        } else {
            kodePreview.setText(barangEdit.getKode());
            comboLokasi.setDisable(true);
            comboKategori.setDisable(true);
        }

        // Logika Upload
        btnUpload.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Pilih Gambar Barang");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
            );
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                selectedImageFile = file;
                pathLabel.setText(file.getName());
            }
        });

        grid.add(new Label("Lokasi:"), 0, 0); grid.add(comboLokasi, 1, 0);
        grid.add(new Label("Kategori:"), 0, 1); grid.add(comboKategori, 1, 1);
        grid.add(new Label("Kode (Auto):"), 0, 2); grid.add(kodePreview, 1, 2);
        grid.add(new Label("Nama Barang:"), 0, 3); grid.add(nama, 1, 3);
        grid.add(new Label("Stok Awal:"), 0, 4); grid.add(stok, 1, 4);
        grid.add(new Label("Gambar:"), 0, 5); 
        
        HBox uploadBox = new HBox(10, btnUpload, pathLabel);
        uploadBox.setAlignment(Pos.CENTER_LEFT);
        grid.add(uploadBox, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // --- LOGIKA AGAR DIALOG TIDAK TUTUP SAAT ERROR ---
        // Ambil tombol Simpan dari dialog
        Button btnSimpan = (Button) dialog.getDialogPane().lookupButton(btnSaveType);
        
        // Tambahkan EventFilter. Ini akan menangkap klik SEBELUM dialog menutup dirinya.
        btnSimpan.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                // Validasi Input
                if (barangEdit == null && (comboLokasi.getValue() == null || comboKategori.getValue() == null)) {
                    throw new IllegalArgumentException("Lokasi dan Kategori harus dipilih!");
                }
                if (nama.getText().trim().isEmpty() || stok.getText().trim().isEmpty()) {
                    throw new IllegalArgumentException("Nama dan Stok harus diisi!");
                }
                
                int stokInt;
                try {
                    stokInt = Integer.parseInt(stok.getText());
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Stok harus berupa angka!");
                }
                
                // Jika lolos validasi, lanjutkan proses simpan
                String finalKode = kodePreview.getText();
                
                // Handle Image Path
                String finalPath = barangEdit != null ? barangEdit.getPathGambar() : "";
                if (selectedImageFile != null) {
                    finalPath = inventarisService.uploadImage(selectedImageFile);
                }

                Barang newItem = new Barang(finalKode, nama.getText(), stokInt, finalPath);
                
                if (barangEdit == null) {
                    inventarisService.tambahBarang(newItem);
                } else {
                    inventarisService.updateBarang(barangEdit, newItem);
                }
                
                refreshTable();
                // Dialog akan tertutup otomatis setelah method ini selesai karena kita TIDAK memanggil event.consume() di sini.

            } catch (IllegalArgumentException ex) {
                // Jika ada error, tampilkan alert DAN cegah dialog tertutup
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Input Error");
                alert.setHeaderText(null);
                alert.setContentText(ex.getMessage());
                alert.show();
                
                event.consume(); // INI KUNCINYA: Membatalkan event penutupan dialog
            }
        });

        dialog.showAndWait();
    }
    
    private void updateKodePreview(ComboBox<String> loc, ComboBox<String> cat, TextField preview) {
        if (loc.getValue() != null && cat.getValue() != null) {
            String newCode = inventarisService.generateNextCode(loc.getValue(), cat.getValue());
            preview.setText(newCode);
        }
    }
}