package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    // MAMP için
    private String url = "jdbc:mysql://localhost:8889/hotel_db?useSSL=false&serverTimezone=UTC";
    private String username = "root";
    private String password = "root";

    private DatabaseConnection() {
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(url, username, password);
            System.out.println("BAĞLANTI BAŞARILI! (MAMP 8889)");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("BAĞLANTI HATASI! MAMP açık mı?");
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        } else {
            try {
                if (instance.getConnection() == null || instance.getConnection().isClosed()) {
                    instance = new DatabaseConnection();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}