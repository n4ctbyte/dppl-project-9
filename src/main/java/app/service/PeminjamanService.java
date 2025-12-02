package app.service;

import app.model.Barang;
import app.model.Peminjaman;
import app.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PeminjamanService {

    private static PeminjamanService instance;
    private List<Peminjaman> semuaPeminjaman;
    private final String JSON_FILENAME = "peminjaman.json";

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
        this.semuaPeminjaman = new ArrayList<>();
        try {
            File targetFile = new File("target/classes/" + JSON_FILENAME);

            if (targetFile.exists()) {
                Gson gson = new Gson();
                Reader reader = new java.io.FileReader(targetFile);
                this.semuaPeminjaman = gson.fromJson(reader, new TypeToken<List<Peminjaman>>(){}.getType());
                reader.close();
            }
            
            if (this.semuaPeminjaman == null) {
                this.semuaPeminjaman = new ArrayList<>();
            }
        } catch (Exception e) {
            this.semuaPeminjaman = new ArrayList<>();
        }
    }

    private void savePeminjaman() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        
        try {
            File targetFile = new File("target/classes/" + JSON_FILENAME);
            if (!targetFile.getParentFile().exists()) {
                targetFile.getParentFile().mkdirs();
            }
            Writer writer = new FileWriter(targetFile);
            gson.toJson(this.semuaPeminjaman, writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            File srcFile = new File("src/main/resources/" + JSON_FILENAME);
            if (srcFile.exists()) {
                Writer writer = new FileWriter(srcFile);
                gson.toJson(this.semuaPeminjaman, writer);
                writer.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void catatPeminjaman(User user, Barang barang, int jumlah, String alasan) {
        Peminjaman peminjamanBaru = new Peminjaman(user, barang, jumlah, alasan);
        
        semuaPeminjaman.add(peminjamanBaru);
        savePeminjaman();
    }
    
    public void hapusRiwayat(Peminjaman peminjaman) {
        semuaPeminjaman.remove(peminjaman);
        savePeminjaman();
    }

    public void hapusSemuaRiwayat() {
        semuaPeminjaman.clear();
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

    public boolean kembalikanPeminjaman(Peminjaman peminjaman, int jumlahKembali, String catatan, boolean isHabisPakai) {
        if (jumlahKembali > peminjaman.getJumlahSisa()) {
            return false;
        }

        int jumlahSisaBaru = peminjaman.getJumlahSisa() - jumlahKembali;
        peminjaman.setJumlahSisa(jumlahSisaBaru);
        
        if (isHabisPakai) {
            peminjaman.setJumlahHabisPakai(peminjaman.getJumlahHabisPakai() + jumlahKembali);
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        peminjaman.setWaktuPengembalian(LocalDateTime.now().format(formatter));
        
        String catatanLengkap = catatan + " (" + jumlahKembali + ")";
        if (isHabisPakai) {
            catatanLengkap += " [HABIS]";
        }
        
        peminjaman.appendCatatan(catatanLengkap);
        
        savePeminjaman();
        return true;
    }
}