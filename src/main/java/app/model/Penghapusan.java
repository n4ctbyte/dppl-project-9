package app.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Penghapusan {
    private String namaBarang;
    private String kodeBarang;
    private String alasan;
    private String tanggalHapus;
    private String adminPenghapus;

    public Penghapusan(String namaBarang, String kodeBarang, String alasan, String adminPenghapus) {
        this.namaBarang = namaBarang;
        this.kodeBarang = kodeBarang;
        this.alasan = alasan;
        this.adminPenghapus = adminPenghapus;
        this.tanggalHapus = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getNamaBarang() { return namaBarang; }
    public String getKodeBarang() { return kodeBarang; }
    public String getAlasan() { return alasan; }
    public String getTanggalHapus() { return tanggalHapus; }
    public String getAdminPenghapus() { return adminPenghapus; }
}