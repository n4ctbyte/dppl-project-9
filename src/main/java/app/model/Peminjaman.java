package app.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Peminjaman {

    private User user;
    private Barang barang;
    private int jumlahAwal;
    private int jumlahSisa;
    private String alasan;
    private String waktuPeminjaman;
    private String waktuPengembalian;
    private String catatan;
    private int getJumlahHabisPakai;

    public Peminjaman(User user, Barang barang, int jumlah, String alasan) {
        this.user = user;
        this.barang = barang;
        this.jumlahAwal = jumlah;
        this.jumlahSisa = jumlah;
        this.getJumlahHabisPakai = 0;
        this.alasan = alasan;
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.waktuPeminjaman = LocalDateTime.now().format(formatter);
        
        this.waktuPengembalian = "";
        this.catatan = "";
    }

    public User getUser() {
        return user;
    }

    public Barang getBarang() {
        return barang;
    }

    public int getJumlahAwal() {
        return jumlahAwal;
    }

    public int getJumlahSisa() {
        return jumlahSisa;
    }

    public String getAlasan() {
        return alasan;
    }

    public String getWaktuPeminjaman() {
        return waktuPeminjaman;
    }

    public String getWaktuPengembalian() {
        return waktuPengembalian;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setJumlahSisa(int jumlahSisa) {
        this.jumlahSisa = jumlahSisa;
    }

    public int getJumlahHabisPakai() { return getJumlahHabisPakai; }

    public void setJumlahHabisPakai(int JumlahHabisPakai) {
        this.getJumlahHabisPakai = JumlahHabisPakai;
    }

    public void setWaktuPengembalian(String waktuPengembalian) {
        this.waktuPengembalian = waktuPengembalian;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }
    
    public void appendCatatan(String catatanBaru) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String timestamp = LocalDateTime.now().format(formatter);
        
        if (this.catatan == null || this.catatan.isEmpty()) {
            this.catatan = "(" + timestamp + ") " + catatanBaru;
        } else {
            this.catatan += "\n(" + timestamp + ") " + catatanBaru;
        }
    }

    @Override
    public String toString() {
        return barang.getNama() + " (Sisa Pinjam: " + jumlahSisa + ")";
    }
}