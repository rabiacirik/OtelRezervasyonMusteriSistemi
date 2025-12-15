package model;

// Abstract Class
public abstract class User {
    protected int id;
    protected String username;
    protected String password;

    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public abstract void showDashboard(); // Her rolün ekranı farklıdır

    // Getter-Setterlar
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public int getId() { return id; }
}