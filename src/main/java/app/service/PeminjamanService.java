package app.service;

import app.model.Barang;
import app.model.Peminjaman;
import app.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PeminjamanService {

    private static PeminjamanService instance;
    private List<Peminjaman> daftarPeminjamanAktif;

    private PeminjamanService() {
        this.daftarPeminjamanAktif = new ArrayList<>();
    }

    public static PeminjamanService getInstance() {
        if (instance == null) {
            instance = new PeminjamanService();
        }
        return instance;
    }

    public void catatPeminjaman(User user, Barang barang, int jumlah, String alasan) {
        Peminjaman peminjamanBaru = new Peminjaman(user, barang, jumlah, alasan);
        daftarPeminjamanAktif.add(peminjamanBaru);
        System.out.println("Peminjaman baru dicatat: " + user.getNama() + " pinjam " + barang.getNama());
    }

    public List<Peminjaman> getPeminjamanAktifByUser(User user) {
        return daftarPeminjamanAktif.stream()
                .filter(p -> p.getUser().getEmail().equals(user.getEmail()))
                .filter(p -> p.getJumlah() > 0)
                .collect(Collectors.toList());
    }

    public boolean kembalikanPeminjaman(Peminjaman peminjaman, int jumlahKembali, String catatan) {
        if (jumlahKembali > peminjaman.getJumlah() || jumlahKembali <= 0) {
            return false;
        }
        
        peminjaman.setJumlah(peminjaman.getJumlah() - jumlahKembali);
        System.out.println("Catatan Pengembalian: " + catatan);

        return true;
    }
}