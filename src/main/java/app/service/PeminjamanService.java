package app.service;

import app.model.Barang;
import app.model.Peminjaman;
import app.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PeminjamanService {

    private static PeminjamanService instance;
    private List<Peminjaman> semuaPeminjaman;
    private final String JSON_PATH = "peminjaman.json";

    private PeminjamanService() {
        loadPeminjaman();
    }

    public static PeminjamanService getInstance() {
        if (instance == null) {
            instance = new PeminjamanService();
        }
        return instance;
    }

    private void loadPeminjaman() {
        try {
            Gson gson = new Gson();
            Type peminjamanListType = new TypeToken<List<Peminjaman>>(){}.getType();

            InputStream is = getClass().getResourceAsStream("/" + JSON_PATH);

            if (is == null) {
                System.err.println("File " + JSON_PATH + " tidak ditemukan, membuat list baru.");
                this.semuaPeminjaman = new ArrayList<>();
                return;
            }

            Reader reader = new InputStreamReader(is);
            this.semuaPeminjaman = gson.fromJson(reader, peminjamanListType);

            if (this.semuaPeminjaman == null) {
                this.semuaPeminjaman = new ArrayList<>();
            }
            
            System.out.println("PeminjamanService: Berhasil memuat " + semuaPeminjaman.size() + " riwayat peminjaman.");

        } catch (Exception e) {
            System.err.println("Gagal membaca " + JSON_PATH + ": " + e.getMessage());
            this.semuaPeminjaman = new ArrayList<>();
        }
    }

    private void savePeminjaman() {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String path = getClass().getResource("/" + JSON_PATH).getPath();
            
            if (path.startsWith("/") && System.getProperty("os.name").toLowerCase().contains("win")) {
                path = path.substring(1);
            }

            Writer writer = new FileWriter(path);
            gson.toJson(this.semuaPeminjaman, writer);
            writer.close();
            
            System.out.println("PeminjamanService: Berhasil menyimpan data ke " + JSON_PATH);
            
        } catch (Exception e) {
            System.err.println("Gagal menyimpan ke " + JSON_PATH + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void catatPeminjaman(User user, Barang barang, int jumlah, String alasan) {
        Peminjaman peminjamanBaru = new Peminjaman(user, barang, jumlah, alasan);
        semuaPeminjaman.add(peminjamanBaru);
        savePeminjaman();
    }
    
    public List<Peminjaman> getAllPeminjaman() {
        return new ArrayList<>(semuaPeminjaman);
    }

    public List<Peminjaman> getPeminjamanAktifByUser(User user) {
        return semuaPeminjaman.stream()
                .filter(p -> p.getUser().getEmail().equals(user.getEmail()))
                .filter(p -> p.getJumlahSisa() > 0)
                .collect(Collectors.toList());
    }

    public boolean kembalikanPeminjaman(Peminjaman peminjaman, int jumlahKembali, String catatan) {
        if (jumlahKembali > peminjaman.getJumlahSisa()) {
            return false;
        }

        int jumlahSisaBaru = peminjaman.getJumlahSisa() - jumlahKembali;
        peminjaman.setJumlahSisa(jumlahSisaBaru);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        peminjaman.setWaktuPengembalian(LocalDateTime.now().format(formatter));
        
        peminjaman.appendCatatan(catatan);
        
        savePeminjaman();
        return true;
    }
}