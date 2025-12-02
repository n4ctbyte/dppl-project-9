package app.service;

import app.model.Barang;
import app.model.Penghapusan;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InventarisService {

    private static InventarisService instance;
    private List<Barang> daftarBarang;
    private List<Penghapusan> logPenghapusan;
    private final String FILE_BARANG = "barang.json";
    private final String FILE_PENGHAPUSAN = "penghapusan.json";

    private InventarisService() {
        loadBarang();
        loadPenghapusan();
    }

    public static InventarisService getInstance() {
        if (instance == null) {
            instance = new InventarisService();
        }
        return instance;
    }

    private void loadBarang() {
        try {
            Gson gson = new Gson();
            InputStream is = getClass().getResourceAsStream("/" + FILE_BARANG);
            if (is == null) {
                daftarBarang = new ArrayList<>();
                return;
            }
            Reader reader = new InputStreamReader(is);
            daftarBarang = gson.fromJson(reader, new TypeToken<List<Barang>>(){}.getType());
            if (daftarBarang == null) daftarBarang = new ArrayList<>();
        } catch (Exception e) {
            daftarBarang = new ArrayList<>();
        }
    }

    private void loadPenghapusan() {
        try {
            Gson gson = new Gson();
            InputStream is = getClass().getResourceAsStream("/" + FILE_PENGHAPUSAN);
            if (is == null) {
                logPenghapusan = new ArrayList<>();
                return;
            }
            Reader reader = new InputStreamReader(is);
            logPenghapusan = gson.fromJson(reader, new TypeToken<List<Penghapusan>>(){}.getType());
            if (logPenghapusan == null) logPenghapusan = new ArrayList<>();
        } catch (Exception e) {
            logPenghapusan = new ArrayList<>();
        }
    }

    private void saveToJson(String fileName, Object data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        
        try {
            String pathTarget = getClass().getResource("/" + fileName).getPath();
            if (pathTarget.startsWith("/") && System.getProperty("os.name").toLowerCase().contains("win")) {
                pathTarget = pathTarget.substring(1);
            }
            try (Writer writer = new FileWriter(pathTarget)) {
                gson.toJson(data, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            File srcFile = new File("src/main/resources/" + fileName);
            if (srcFile.exists()) {
                try (Writer writer = new FileWriter(srcFile)) {
                    gson.toJson(data, writer);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String generateNextCode(String lokasi, String kategori) {
        String prefix = lokasi + "-" + kategori + "-";
        int maxNumber = 0;

        for (Barang b : daftarBarang) {
            if (b.getKode().startsWith(prefix)) {
                try {
                    String numberPart = b.getKode().substring(prefix.length());
                    int number = Integer.parseInt(numberPart);
                    if (number > maxNumber) {
                        maxNumber = number;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }

        return prefix + String.format("%03d", maxNumber + 1);
    }

    public String uploadImage(File sourceFile) {
        if (sourceFile == null) return "";
        
        try {
            String fileName = sourceFile.getName();
            
            File srcImagesDir = new File("src/main/resources/images/");
            if (!srcImagesDir.exists()) srcImagesDir.mkdirs();
            File srcDestFile = new File(srcImagesDir, fileName);
            Files.copy(sourceFile.toPath(), srcDestFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            String targetDir = getClass().getResource("/").getPath();
            if (targetDir.startsWith("/") && System.getProperty("os.name").toLowerCase().contains("win")) {
                targetDir = targetDir.substring(1);
            }
            File targetImagesDir = new File(targetDir + "images/");
            if (!targetImagesDir.exists()) targetImagesDir.mkdirs();
            File targetDestFile = new File(targetImagesDir, fileName);
            Files.copy(sourceFile.toPath(), targetDestFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            return "images/" + fileName;
        } catch (Exception e) {
            return "";
        }
    }

    public List<Barang> getAllBarang() {
        return new ArrayList<>(daftarBarang);
    }
    
    public List<Penghapusan> getLogPenghapusan() {
        return new ArrayList<>(logPenghapusan);
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
            saveToJson(FILE_BARANG, daftarBarang);
            return true;
        }
        return false;
    }

    public void kembalikanBarang(Barang barang, int jumlah) {
        if (jumlah > 0) {
            barang.setStok(barang.getStok() + jumlah);
            saveToJson(FILE_BARANG, daftarBarang);
        }
    }

    public void tambahBarang(Barang barang) {
        daftarBarang.add(barang);
        saveToJson(FILE_BARANG, daftarBarang);
    }

    public void updateBarang(Barang oldBarang, Barang newBarang) {
        int index = daftarBarang.indexOf(oldBarang);
        if (index >= 0) {
            daftarBarang.set(index, newBarang);
            saveToJson(FILE_BARANG, daftarBarang);
        }
    }

    public void hapusBarang(Barang barang, String alasan, String admin) {
        daftarBarang.remove(barang);
        saveToJson(FILE_BARANG, daftarBarang);
        
        Penghapusan log = new Penghapusan(barang.getNama(), barang.getKode(), alasan, admin);
        logPenghapusan.add(log);
        saveToJson(FILE_PENGHAPUSAN, logPenghapusan);
    }
}