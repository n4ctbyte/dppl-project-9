package app.view;

import app.SiurApp;
import app.model.Peminjaman;
import app.service.AuthService;
import app.service.PeminjamanService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.Duration;

public class HalamanRiwayat {

    private SiurApp mainApp;
    private PeminjamanService peminjamanService;
    private AuthService authService;
    private TableView<Peminjaman> tableView;

    public HalamanRiwayat(SiurApp mainApp) {
        this.mainApp = mainApp;
        this.peminjamanService = PeminjamanService.getInstance();
        this.authService = AuthService.getInstance();
    }

    public Node getView() {
        VBox contentContainer = new VBox(20);
        contentContainer.setPadding(new Insets(25));
        
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Riwayat Peminjaman");
        title.getStyleClass().add("label-title");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        header.getChildren().addAll(title, spacer);

        if (authService.isAdmin()) {
            Button btnHapusSemua = new Button("Hapus Semua Riwayat");
            btnHapusSemua.getStyleClass().add("btn-invert-red");
            setupButtonScaleAnimation(btnHapusSemua);
            btnHapusSemua.setOnAction(e -> handleHapusSemua());
            header.getChildren().add(btnHapusSemua);
        }
        
        setupTableView();
        loadRiwayatData();

        contentContainer.getChildren().addAll(header, tableView);
        VBox.setVgrow(tableView, javafx.scene.layout.Priority.ALWAYS);
        
        return contentContainer;
    }
    
    private void setupButtonScaleAnimation(Node node) {
        ScaleTransition stIn = new ScaleTransition(Duration.millis(150), node);
        stIn.setToX(1.05);
        stIn.setToY(1.05);
        
        ScaleTransition stOut = new ScaleTransition(Duration.millis(150), node);
        stOut.setToX(1.0);
        stOut.setToY(1.0);
        
        node.setOnMouseEntered(e -> stIn.playFromStart());
        node.setOnMouseExited(e -> stOut.playFromStart());
    }

    private void setupTableView() {
        tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Peminjaman, String> colPeminjam = new TableColumn<>("Peminjam");
        colPeminjam.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getUser().getNama())
        );

        TableColumn<Peminjaman, String> colBarang = new TableColumn<>("Barang");
        colBarang.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getBarang().getNama())
        );

        TableColumn<Peminjaman, Integer> colJumlah = new TableColumn<>("Awal");
        colJumlah.setCellValueFactory(new PropertyValueFactory<>("jumlahAwal"));
        colJumlah.setMaxWidth(60);
        colJumlah.setMinWidth(60);

        TableColumn<Peminjaman, Integer> colSisa = new TableColumn<>("Sisa");
        colSisa.setCellValueFactory(new PropertyValueFactory<>("jumlahSisa"));
        colSisa.setMaxWidth(60);
        colSisa.setMinWidth(60);
        
        TableColumn<Peminjaman, Integer> colHabis = new TableColumn<>("Habis");
        colHabis.setCellValueFactory(new PropertyValueFactory<>("jumlahHabisPakai"));
        colHabis.setStyle("-fx-alignment: CENTER; -fx-text-fill: #D32F2F; -fx-font-weight: bold;");
        colHabis.setMaxWidth(60);
        colHabis.setMinWidth(60);

        TableColumn<Peminjaman, String> colWaktuPinjam = new TableColumn<>("Tgl Pinjam");
        colWaktuPinjam.setCellValueFactory(new PropertyValueFactory<>("waktuPeminjaman"));
        colWaktuPinjam.setMinWidth(140);

        TableColumn<Peminjaman, String> colWaktuKembali = new TableColumn<>("Tgl Kembali");
        colWaktuKembali.setCellValueFactory(new PropertyValueFactory<>("waktuPengembalian"));
        colWaktuKembali.setMinWidth(140);

        TableColumn<Peminjaman, String> colAlasan = new TableColumn<>("Alasan");
        colAlasan.setCellValueFactory(new PropertyValueFactory<>("alasan"));
        colAlasan.setCellFactory(createWrappingCellFactory());

        TableColumn<Peminjaman, String> colCatatan = new TableColumn<>("Catatan");
        colCatatan.setCellValueFactory(new PropertyValueFactory<>("catatan"));
        colCatatan.setCellFactory(createWrappingCellFactory());

        tableView.getColumns().addAll(colPeminjam, colBarang, colJumlah, colSisa, colHabis, colWaktuPinjam, colWaktuKembali, colAlasan, colCatatan);

        if (authService.isAdmin()) {
            TableColumn<Peminjaman, Void> colAksi = new TableColumn<>("Aksi");
            colAksi.setStyle("-fx-alignment: CENTER;");
            colAksi.setMinWidth(60);
            colAksi.setMaxWidth(60);
            
            colAksi.setCellFactory(param -> new TableCell<>() {
                private final Button btnHapus = new Button("🗑");

                {
                    btnHapus.getStyleClass().add("btn-icon-delete");
                    btnHapus.setOnAction(event -> {
                        Peminjaman item = getTableView().getItems().get(getIndex());
                        handleDelete(item);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : btnHapus);
                }
            });
            
            tableView.getColumns().add(colAksi);
        }
    }
    
    private void handleDelete(Peminjaman item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Hapus Riwayat");
        alert.setHeaderText("Menghapus Riwayat Peminjaman");
        alert.setContentText("Apakah Anda yakin ingin menghapus data ini?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            peminjamanService.hapusRiwayat(item);
            loadRiwayatData();
        }
    }

    private void handleHapusSemua() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Hapus Semua Riwayat");
        alert.setHeaderText("PERINGATAN: Menghapus Seluruh Data");
        alert.setContentText("Apakah Anda yakin ingin menghapus SEMUA riwayat peminjaman?\nTindakan ini tidak dapat dibatalkan!");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            peminjamanService.hapusSemuaRiwayat();
            loadRiwayatData();
        }
    }
    
    private Callback<TableColumn<Peminjaman, String>, TableCell<Peminjaman, String>> createWrappingCellFactory() {
        return param -> new TableCell<>() {
            private final Text textNode = new Text();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    textNode.setText(item);
                    textNode.wrappingWidthProperty().bind(getTableColumn().widthProperty().subtract(20));
                    setGraphic(textNode);
                }
            }
        };
    }
    
    public void loadRiwayatData() {
        if (tableView == null) {
            setupTableView();
        }
        
        List<Peminjaman> riwayat;
        if (authService.isAdmin()) {
            riwayat = peminjamanService.getAllPeminjaman();
        } else {
            riwayat = peminjamanService.getAllPeminjaman().stream()
                        .filter(p -> p.getUser().getEmail().equals(authService.getUserAktif().getEmail()))
                        .collect(Collectors.toList());
        }
        tableView.setItems(FXCollections.observableArrayList(riwayat));
        tableView.refresh();
    }
}