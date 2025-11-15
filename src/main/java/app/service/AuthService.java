package app.service;

import app.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AuthService {

    private static AuthService instance;
    private List<User> userDatabase;
    private User userAktif;

    private AuthService() {
        loadUsersFromJson();
    }

    private void loadUsersFromJson() {
        try {
            Gson gson = new Gson();
            Type userListType = new TypeToken<List<User>>(){}.getType();

            InputStream is = getClass().getResourceAsStream("/users.json");

            if (is == null) {
                System.err.println("File users.json tidak ditemukan di src/main/resources!");
                userDatabase = new ArrayList<>();
                return;
            }

            Reader reader = new InputStreamReader(is);
            userDatabase = gson.fromJson(reader, userListType);

            if (userDatabase == null) {
                userDatabase = new ArrayList<>();
            }
            
            System.out.println("AuthService: Berhasil memuat " + userDatabase.size() + " pengguna dari users.json.");

        } catch (Exception e) {
            System.err.println("Gagal membaca users.json: " + e.getMessage());
            userDatabase = new ArrayList<>();
        }
    }

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public boolean login(String email, String password) {
        
        for (User user : userDatabase) {
            if (user.getEmail().equalsIgnoreCase(email) && user.getPassword().equals(password)) {
                userAktif = user;
                return true;
            }
        }

        return false;
    }

    public void logout() {
        userAktif = null;
    }

    public User getUserAktif() {
        return userAktif;
    }

    public boolean isAdmin() {
        return userAktif != null && userAktif.getRole().equals("ADMIN");
    }
}