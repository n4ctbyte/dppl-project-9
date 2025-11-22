package app.view;

import app.SiurApp;
import app.service.LaporanService;
import java.io.File;
import java.time.LocalDate;
import java.util.Map;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.util.StringConverter;

public class HalamanStatistik {

    private SiurApp mainApp;
    private LaporanService laporanService;
    private BarChart<String, Number> barChart;
    private ComboBox<String> intervalBox;

    public HalamanStatistik(SiurApp mainApp) {
        this.mainApp = mainApp;
        this.laporanService = LaporanService.getInstance();
    }

    public Node getView() {
        VBox contentContainer = new VBox(20);
        contentContainer.setPadding(new Insets(25));
        contentContainer.setStyle("-fx-background-color: #f5f7fa;");

        Label title = new Label("Statistik & Laporan");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: #2E3348;");

        HBox filterBox = new HBox(15);
        filterBox.setPadding(new Insets(15));
        filterBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 1);");
        filterBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label lblInterval = new Label("Pilih Interval:");
        lblInterval.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        intervalBox = new ComboBox<>();
        intervalBox.getItems().addAll(
            "1 Minggu Terakhir",
            "2 Minggu Terakhir",
            "1 Bulan Terakhir",
            "3 Bulan Terakhir",
            "6 Bulan Terakhir",
            "1 Tahun Terakhir"
        );
        intervalBox.setValue("1 Bulan Terakhir");
        intervalBox.setStyle("-fx-font-size: 14px;");
        
        intervalBox.setOnAction(e -> refreshChart());
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button btnPdf = new Button("Unduh Laporan PDF");
        
        String stylePdf = "-fx-background-color: #D32F2F; -fx-background-radius: 8; -fx-text-fill: #FFFFFF; -fx-font-size: 14px; -fx-padding: 8 15; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 1, 1); -fx-cursor: hand; -fx-border-color: transparent; -fx-border-width: 1.5; -fx-border-radius: 8;";
        
        String stylePdfHover = "-fx-background-color: #FFFFFF; -fx-background-radius: 8; -fx-text-fill: #D32F2F; -fx-font-size: 14px; -fx-padding: 8 15; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 1, 1); -fx-cursor: hand; -fx-border-color: #D32F2F; -fx-border-width: 1.5; -fx-border-radius: 8;";
        
        btnPdf.setStyle(stylePdf);
        setupButtonHoverEffect(btnPdf, 1.05, stylePdf, stylePdfHover);
        
        btnPdf.setOnAction(e -> downloadPdf());

        filterBox.getChildren().addAll(lblInterval, intervalBox, spacer, btnPdf);

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Nama Barang");
        
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Jumlah Peminjaman");
        
        yAxis.setTickUnit(1);
        yAxis.setMinorTickCount(0);
        yAxis.setForceZeroInRange(true);
        yAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                if (object.doubleValue() % 1 == 0) {
                    return String.format("%.0f", object.doubleValue());
                }
                return "";
            }

            @Override
            public Number fromString(String string) {
                return Double.valueOf(string);
            }
        });
        
        barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Tren Peminjaman Barang");
        barChart.setLegendVisible(false);
        barChart.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 1);");
        
        refreshChart();

        contentContainer.getChildren().addAll(title, filterBox, barChart);
        VBox.setVgrow(barChart, Priority.ALWAYS);
        
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
    
    private LocalDate calculateStartDate(String interval) {
        LocalDate now = LocalDate.now();
        switch (interval) {
            case "1 Minggu Terakhir": return now.minusWeeks(1);
            case "2 Minggu Terakhir": return now.minusWeeks(2);
            case "1 Bulan Terakhir": return now.minusMonths(1);
            case "3 Bulan Terakhir": return now.minusMonths(3);
            case "6 Bulan Terakhir": return now.minusMonths(6);
            case "1 Tahun Terakhir": return now.minusYears(1);
            default: return now.minusMonths(1);
        }
    }

    private void refreshChart() {
        barChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Peminjaman");

        String selectedInterval = intervalBox.getValue();
        LocalDate start = calculateStartDate(selectedInterval);
        LocalDate end = LocalDate.now();

        Map<String, Integer> stats = laporanService.getStatistikPeminjaman(start, end);
        
        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        
        barChart.getData().add(series);
    }

    private void downloadPdf() {
        String selectedInterval = intervalBox.getValue();
        LocalDate start = calculateStartDate(selectedInterval);
        LocalDate end = LocalDate.now();
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Simpan Laporan PDF");
        fileChooser.setInitialFileName("Laporan_Inventaris_" + selectedInterval.replace(" ", "_") + ".pdf");
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            laporanService.generateLaporanPDF(file.getAbsolutePath(), start, end);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sukses");
            alert.setHeaderText(null);
            alert.setContentText("Laporan PDF berhasil disimpan!");
            alert.show();
        }
    }
}