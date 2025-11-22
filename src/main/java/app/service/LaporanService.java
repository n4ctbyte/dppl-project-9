package app.service;

import app.model.Barang;
import app.model.Peminjaman;
import app.model.Penghapusan;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LaporanService {

    private static LaporanService instance;
    private PeminjamanService peminjamanService;
    private InventarisService inventarisService;

    private LaporanService() {
        peminjamanService = PeminjamanService.getInstance();
        inventarisService = InventarisService.getInstance();
    }

    public static LaporanService getInstance() {
        if (instance == null) {
            instance = new LaporanService();
        }
        return instance;
    }

    public Map<String, Integer> getStatistikPeminjaman(LocalDate start, LocalDate end) {
        List<Peminjaman> list = peminjamanService.getAllPeminjaman();
        Map<String, Integer> stats = new HashMap<>();
        
        for (Peminjaman p : list) {
            LocalDate tgl = LocalDate.parse(p.getWaktuPeminjaman().substring(0, 10));
            if ((tgl.isEqual(start) || tgl.isAfter(start)) && (tgl.isEqual(end) || tgl.isBefore(end))) {
                stats.put(p.getBarang().getNama(), stats.getOrDefault(p.getBarang().getNama(), 0) + p.getJumlahAwal());
            }
        }
        return stats;
    }

    public void generateLaporanPDF(String filePath, LocalDate start, LocalDate end) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            document.add(new Paragraph("Laporan Statistik Inventaris SIUR"));
            document.add(new Paragraph("Periode: " + start + " s/d " + end));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("1. Statistik Peminjaman Barang"));
            PdfPTable tablePinjam = new PdfPTable(2);
            tablePinjam.addCell("Nama Barang");
            tablePinjam.addCell("Total Dipinjam");
            
            Map<String, Integer> stats = getStatistikPeminjaman(start, end);
            for (Map.Entry<String, Integer> entry : stats.entrySet()) {
                tablePinjam.addCell(entry.getKey());
                tablePinjam.addCell(entry.getValue().toString());
            }
            document.add(tablePinjam);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("2. Log Penghapusan Barang"));
            PdfPTable tableHapus = new PdfPTable(3);
            tableHapus.addCell("Barang");
            tableHapus.addCell("Alasan");
            tableHapus.addCell("Waktu");

            List<Penghapusan> logs = inventarisService.getLogPenghapusan();
            for (Penghapusan log : logs) {
                LocalDate tgl = LocalDate.parse(log.getTanggalHapus().substring(0, 10));
                if ((tgl.isEqual(start) || tgl.isAfter(start)) && (tgl.isEqual(end) || tgl.isBefore(end))) {
                    tableHapus.addCell(log.getNamaBarang() + " (" + log.getKodeBarang() + ")");
                    tableHapus.addCell(log.getAlasan());
                    tableHapus.addCell(log.getTanggalHapus());
                }
            }
            document.add(tableHapus);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}