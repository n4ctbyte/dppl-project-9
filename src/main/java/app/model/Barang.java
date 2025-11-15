package app.model;

public class Barang {

    private String kode;
    private String nama;
    private int stok;
    private String pathGambar;

    public Barang(String kode, String nama, int stok, String pathGambar) {
        this.kode = kode;
        this.nama = nama;
        this.stok = stok;
        this.pathGambar = pathGambar;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        if (stok >= 0) {
            this.stok = stok;
        }
    }

    public String getPathGambar() {
        return pathGambar;
    }

    public void setPathGambar(String pathGambar) {
        this.pathGambar = pathGambar;
    }

    @Override
    public String toString() {
        return getNama() + " (Stok: " + getStok() + ")";
    }
}