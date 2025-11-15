package app.model;

public class User {
    private String email;
    private String password;
    private String role;
    private String nama;

    public User(String email, String password, String role, String nama) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }
}