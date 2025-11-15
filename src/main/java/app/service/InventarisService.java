package app.service;

import app.model.Barang;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InventarisService {

    private static InventarisService instance;
    private List<Barang> daftarBarang;

    private InventarisService() {
        loadBarangFromJson();
    }
    
    private void loadBarangFromJson() {
        try {
            Gson gson = new Gson();
            Type barangListType = new TypeToken<List<Barang>>(){}.getType();

            InputStream is = getClass().getResourceAsStream("barang.json");

            if (is == null) {
                System.err.println("File barang.json tidak ditemukan di src/main/resources!");
                daftarBarang = new ArrayList<>();
                return;
            }

            Reader reader = new InputStreamReader(is);
            daftarBarang = gson.fromJson(reader, barangListType);

            if (daftarBarang == null) {
                daftarBarang = new ArrayList<>();
            }
            
            System.out.println("InventarisService: Berhasil memuat " + daftarBarang.size() + " barang dari barang.json.");

        } catch (Exception e) {
            System.err.println("Gagal membaca barang.json: " + e.getMessage());
            daftarBarang = new ArrayList<>();
        }
    }

    public static InventarisService getInstance() {
        if (instance == null) {
            instance = new InventarisService();
        }
        return instance;
    }

    public List<Barang> getAllBarang() {
        return new ArrayList<>(daftarBarang);
    }
    
    public List<Barang> filterBarang(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return daftarBarang.stream()
                .filter(b -> b.getNama().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    public boolean pinjamBarang(Barang barang, int jumlah) {
        if (jumlah > 0 && barang.getStok() >= jumlah) {
            barang.setStok(barang.getStok() - jumlah);
            return true;
        }
        return false;
    }

    public void kembalikanBarang(Barang barang, int jumlah) {
        if (jumlah > 0) {
            barang.setStok(barang.getStok() + jumlah);
        }
    }
}