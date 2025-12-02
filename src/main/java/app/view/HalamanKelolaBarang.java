package app.view;

import app.SiurApp;
import app.model.Barang;
import app.service.InventarisService;
import java.io.File;
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

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button btnTambah = new Button("＋ Tambah Barang");
        btnTambah.getStyleClass().add("btn-invert-blue");
        setupButtonScaleAnimation(btnTambah, 1.05);
        
        btnTambah.setOnAction(e -> showFormDialog(null));
        
        header.getChildren().addAll(spacer, btnTambah);

        setupTableView();
        refreshTable();

        contentContainer.getChildren().addAll(header, tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS);
        
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
                
                btnEdit.getStyleClass().add("btn-icon-edit");
                btnEdit.setTooltip(new Tooltip("Edit Data"));
                setupButtonScaleOnly(btnEdit);
                
                btnHapus.getStyleClass().add("btn-icon-delete");
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

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            String alasan = dialog.getEditor().getText();
            if (alasan == null || alasan.trim().isEmpty()) {
                event.consume();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Gagal");
                alert.setHeaderText(null);
                alert.setContentText("Alasan penghapusan harus diisi!");
                alert.show();
            } else {
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

        if (barangEdit == null) {
            comboLokasi.setOnAction(e -> updateKodePreview(comboLokasi, comboKategori, kodePreview));
            comboKategori.setOnAction(e -> updateKodePreview(comboLokasi, comboKategori, kodePreview));
        } else {
            kodePreview.setText(barangEdit.getKode());
            comboLokasi.setDisable(true);
            comboKategori.setDisable(true);
        }

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

        Button btnSimpan = (Button) dialog.getDialogPane().lookupButton(btnSaveType);
        
        btnSimpan.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                if (barangEdit == null && (comboLokasi.getValue() == null || comboKategori.getValue() == null)) {
                    throw new IllegalArgumentException("Lokasi dan Kategori harus dipilih!");
                }
                if (nama.getText().trim().isEmpty() || stok.getText().trim().isEmpty()) {
                    throw new IllegalArgumentException("Nama dan Stok harus diisi!");
                }
                
                if (barangEdit == null && selectedImageFile == null) {
                    throw new IllegalArgumentException("Gambar barang wajib diupload!");
                }
                
                int stokInt;
                try {
                    stokInt = Integer.parseInt(stok.getText());
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Stok harus berupa angka!");
                }
                
                String finalKode = kodePreview.getText();
                
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

            } catch (IllegalArgumentException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Input Error");
                alert.setHeaderText(null);
                alert.setContentText(ex.getMessage());
                alert.show();
                event.consume(); 
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