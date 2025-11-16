package app.view;

import app.SiurApp;
import app.model.Peminjaman;
import app.service.AuthService;
import app.service.PeminjamanService;
import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

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
        contentContainer.setStyle("-fx-background-color: #f5f7fa;");

        Label title = new Label("Riwayat Peminjaman");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        
        setupTableView();
        loadRiwayatData();

        contentContainer.getChildren().addAll(title, tableView);
        return contentContainer;
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

        TableColumn<Peminjaman, Integer> colJumlah = new TableColumn<>("Jumlah Pinjam");
        colJumlah.setCellValueFactory(new PropertyValueFactory<>("jumlahAwal"));

        TableColumn<Peminjaman, Integer> colSisa = new TableColumn<>("Sisa Pinjam");
        colSisa.setCellValueFactory(new PropertyValueFactory<>("jumlahSisa"));

        TableColumn<Peminjaman, String> colWaktuPinjam = new TableColumn<>("Waktu Pinjam");
        colWaktuPinjam.setCellValueFactory(new PropertyValueFactory<>("waktuPeminjaman"));

        TableColumn<Peminjaman, String> colWaktuKembali = new TableColumn<>("Waktu Kembali");
        colWaktuKembali.setCellValueFactory(new PropertyValueFactory<>("waktuPengembalian"));

        TableColumn<Peminjaman, String> colAlasan = new TableColumn<>("Alasan");
        colAlasan.setCellValueFactory(new PropertyValueFactory<>("alasan"));
        
        TableColumn<Peminjaman, String> colCatatan = new TableColumn<>("Catatan");
        colCatatan.setCellValueFactory(new PropertyValueFactory<>("catatan"));

        tableView.getColumns().addAll(colPeminjam, colBarang, colJumlah, colSisa, colWaktuPinjam, colWaktuKembali, colAlasan, colCatatan);
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