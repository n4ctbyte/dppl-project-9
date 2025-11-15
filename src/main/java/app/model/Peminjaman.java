package app.model;

import java.time.LocalDate;

public class Peminjaman {

    private User user;
    private Barang barang;
    private int jumlah;
    private String alasan;
    private String tanggalPinjam;

    public Peminjaman(User user, Barang barang, int jumlah, String alasan) {
        this.user = user;
        this.barang = barang;
        this.jumlah = jumlah;
        this.alasan = alasan;
        this.tanggalPinjam = LocalDate.now().toString();
    }

    public User getUser() {
        return user;
    }

    public Barang getBarang() {
        return barang;
    }

    public int getJumlah() {
        return jumlah;
    }

    public String getAlasan() {
        return alasan;
    }

    public String getTanggalPinjam() {
        return tanggalPinjam;
    }
    
    public LocalDate getTanggalPinjamAsLocalDate() {
        return LocalDate.parse(this.tanggalPinjam);
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    @Override
    public String toString() {
        return barang.getNama() + " (Sisa Pinjam: " + jumlah + ")";
    }
}