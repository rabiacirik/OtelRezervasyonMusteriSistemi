package dao;

import config.DatabaseConnection;
import model.CustomerDTO;
import patterns.strategy.PricingStrategy;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Veritabanı Erişim Nesnesi
 * Bu sınıf, Arayüz ile Veritabanı arasındaki köprüdür.
 * Aynı zamanda Facade Tasarım Deseni mantığıyla, karmaşık veritabanı işlemlerini
 * arayüzden gizleyerek basit metotlar halinde sunar.
 */
public class HotelDAO {

    // --- 1. GİRİŞ İŞLEMİ BÜYÜK/KÜÇÜK HARF DUYARLI)
    /**
     * Kullanıcı girişi kontrolü.
     * Kullanıcı Adı, E-posta veya TC Kimlik No ile giriş yapılabilir.
     */

    public String login(String input, String password) {
        String role = null;
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            // BINARY komutu, verinin byte-byte (birebir) eşleşmesini zorunlu kılar.
            String sql = "SELECT role FROM users WHERE " +
                    "(BINARY username = ? OR BINARY email = ? OR tc_no = ?) " +
                    "AND BINARY password_hash = ?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, input);
            ps.setString(2, input);
            ps.setString(3, input);
            ps.setString(4, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) role = rs.getString("role");
        } catch (Exception e) { e.printStackTrace(); }
        return role;
    }

    /**
     * Giriş yapan kullanıcının veritabanındaki ID'sini bulur.
     */
    public int getUserId(String input) {
        int id = 0;
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            String sql = "SELECT user_id FROM users WHERE username = ? OR email = ? OR tc_no = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, input); ps.setString(2, input); ps.setString(3, input);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) id = rs.getInt("user_id");
        } catch (Exception e) { e.printStackTrace(); }
        return id;
    }

    // --- 3. MÜŞTERİ KAYIT (BUILDER DESENİ ENTEGRASYONU) ---
    /**
     * Yeni müşteri kaydı oluşturur.
     * Parametre olarak Builder Tasarım Deseni ile oluşturulmuş CustomerDTO nesnesini alır.
     * Bu sayede çok parametreli veri transferi temiz bir şekilde yönetilir.
     */
    public boolean registerCustomer(CustomerDTO customer) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            String sql = "INSERT INTO users (username, password_hash, first_name, last_name, phone, email, tc_no, role) VALUES (?, ?, ?, ?, ?, ?, ?, 'CUSTOMER')";
            PreparedStatement ps = conn.prepareStatement(sql);
            // DTO nesnesinden verileri çekip sorguya yerleştiriyoruz
            ps.setString(1, customer.getUsername());
            ps.setString(2, customer.getPassword());
            ps.setString(3, customer.getFirstName());
            ps.setString(4, customer.getLastName());
            ps.setString(5, customer.getPhone());
            ps.setString(6, customer.getEmail());
            ps.setString(7, customer.getTcNo());
            ps.executeUpdate();
            return true;
        } catch (Exception e) { return false; }
    }

    // --- 4. MÜŞTERİ ARAMA ---
    /**
     * Personel panelinde müşteri aramak için kullanılır.
     * Ad, Soyad, Telefon veya TC No içinde geçen ifadeyi arar (LIKE sorgusu).
     */
    public Vector<Vector<Object>> searchCustomers(String keyword) {
        Vector<Vector<Object>> data = new Vector<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            String sql = "SELECT user_id, first_name, last_name, phone, email, tc_no FROM users WHERE role = 'CUSTOMER' " +
                    "AND (first_name LIKE ? OR last_name LIKE ? OR phone LIKE ? OR tc_no LIKE ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            String search = "%" + keyword + "%";
            ps.setString(1, search); ps.setString(2, search); ps.setString(3, search); ps.setString(4, search);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("user_id")); row.add(rs.getString("first_name")); row.add(rs.getString("last_name"));
                row.add(rs.getString("phone")); row.add(rs.getString("email")); row.add(rs.getString("tc_no"));
                data.add(row);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return data;
    }

    // --- 5. ODA İŞLEMLERİ ---
    /**
     * Sisteme yeni oda ekler.
     */
    public boolean addRoomToDB(String roomNo, String type, double price, int capacity) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            String sql = "INSERT INTO rooms (room_number, room_type, price, capacity, status) VALUES (?, ?, ?, ?, 'MÜSAİT')";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, roomNo); ps.setString(2, type); ps.setDouble(3, price); ps.setInt(4, capacity);
            ps.executeUpdate(); return true;
        } catch (Exception e) { return false; }
    }

    /**
     * Mevcut oda bilgilerini günceller.
     */
    public boolean updateRoom(String roomNo, String type, double price, int capacity) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            String sql = "UPDATE rooms SET room_type = ?, price = ?, capacity = ? WHERE room_number = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, type); ps.setDouble(2, price); ps.setInt(3, capacity); ps.setString(4, roomNo);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }

    /**
     * Tüm odaları listeler.
     * Oda numaralarını string yerine sayısal mantıkla sıralar (1, 2, 10 şeklinde).
     */
    public Vector<Vector<Object>> getAllRooms() {
        Vector<Vector<Object>> data = new Vector<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            String sql = "SELECT * FROM rooms ORDER BY CAST(room_number AS UNSIGNED), room_number";
            PreparedStatement ps = conn.prepareStatement(sql);ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("room_number")); row.add(rs.getString("room_type"));
                row.add(rs.getInt("capacity")); row.add(rs.getDouble("price")); row.add(rs.getString("status"));
                data.add(row);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return data;
    }

    /**
     * Girilen oda numarasının sistemde var olup olmadığını kontrol eder.
     */
    public boolean isRoomValid(String roomNo) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            String sql = "SELECT 1 FROM rooms WHERE room_number = ?";
            PreparedStatement ps = conn.prepareStatement(sql); ps.setString(1, roomNo);
            ResultSet rs = ps.executeQuery(); return rs.next();
        } catch (Exception e) { return false; }
    }

    // --- 6. AKILLI ARAMA ---
    /**
     * Müşteri için uygun odaları arar.
     * Tarih çakışmalarını ve kapasiteyi kontrol eder.
     * "NOT EXISTS" sorgusu ile belirtilen tarihlerde dolu olan odaları eler.
     */
    public List<Vector<Object>> searchRoomsSmart(String startDate, String endDate, int personCount, String type) {
        List<Vector<Object>> availableRooms = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            String sql = "SELECT * FROM rooms r WHERE r.room_type = ? AND r.capacity >= ? " +
                    "AND NOT EXISTS (SELECT 1 FROM reservations res WHERE res.room_number = r.room_number AND (res.status != 'İPTAL') " +
                    "AND ((res.start_date <= ? AND res.end_date >= ?) OR (res.start_date <= ? AND res.end_date >= ?) OR (? <= res.start_date AND ? >= res.end_date)))";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, type); ps.setInt(2, personCount);
            ps.setString(3, endDate); ps.setString(4, startDate); ps.setString(5, startDate); ps.setString(6, endDate); ps.setString(7, startDate); ps.setString(8, endDate);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("room_number")); row.add(rs.getString("room_type"));
                row.add(rs.getDouble("price")); row.add(rs.getInt("capacity")); row.add("MÜSAİT");
                availableRooms.add(row);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return availableRooms;
    }

    // --- 7. REZERVASYON YAPMA (STRATEGY DESENİ ENTEGRASYONU) ---
    /**
     * Rezervasyon işlemini gerçekleştirir.
     * 1. Odanın kapasitesini kontrol eder.
     * 2. Tarihlerin uygunluğunu (çakışma) kontrol eder.
     * 3. Strategy Tasarım Deseni ile fiyatı hesaplar (Normal veya İndirimli).
     */
    public boolean makeReservation(int userId, String roomNo, String startDate, String endDate, int guestCount, PricingStrategy pricingStrategy) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {

            // 1. Kapasite ve Baz Fiyat Kontrolü
            int roomCapacity = 0;
            double pricePerNight = 0;
            String roomSql = "SELECT capacity, price FROM rooms WHERE room_number = ?";
            PreparedStatement psRoom = conn.prepareStatement(roomSql); psRoom.setString(1, roomNo);
            ResultSet rsRoom = psRoom.executeQuery();
            if (rsRoom.next()) {
                roomCapacity = rsRoom.getInt("capacity");
                pricePerNight = rsRoom.getDouble("price");
            }
            // Kapasite yetersizse işlemi durdur
            if (guestCount > roomCapacity) return false;

            // 2. Çakışma Kontrolü
            String checkSql = "SELECT COUNT(*) FROM reservations WHERE room_number = ? AND status != 'İPTAL' AND ((start_date <= ? AND end_date >= ?) OR (start_date <= ? AND end_date >= ?) OR (? <= start_date AND ? >= end_date))";
            PreparedStatement psCheck = conn.prepareStatement(checkSql);
            psCheck.setString(1, roomNo); psCheck.setString(2, endDate); psCheck.setString(3, startDate);
            psCheck.setString(4, startDate); psCheck.setString(5, endDate); psCheck.setString(6, startDate); psCheck.setString(7, endDate);
            ResultSet rs = psCheck.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) return false; // Oda Dolu

            // 3. Fiyat Hesaplama (STRATEGY DESENİ KULLANIMI)
            java.time.LocalDate d1 = java.time.LocalDate.parse(startDate);
            java.time.LocalDate d2 = java.time.LocalDate.parse(endDate);
            long days = java.time.temporal.ChronoUnit.DAYS.between(d1, d2);
            if(days <= 0) days = 1;

            // Fiyat hesaplamasını seçilen stratejiye (PricingStrategy) bırakıyoruz.
            // Bu sayede algoritma dinamik olarak değişebiliyor (Normal vs İndirimli).
            double totalPrice = pricingStrategy.calculateTotal(pricePerNight, days);

            // 4. Rezervasyonu Kaydet
            String sql = "INSERT INTO reservations (user_id, room_number, start_date, end_date, total_price, status) VALUES (?, ?, ?, ?, ?, 'Beklemede')";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId); ps.setString(2, roomNo); ps.setString(3, startDate); ps.setString(4, endDate); ps.setDouble(5, totalPrice);
            ps.executeUpdate();

            // Odayı REZERVE durumuna çek
            String updateRoom = "UPDATE rooms SET status = 'REZERVE' WHERE room_number = ?";
            PreparedStatement ps2 = conn.prepareStatement(updateRoom); ps2.setString(1, roomNo); ps2.executeUpdate();
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // --- 8. REZERVASYON FİLTRELEME ---
    /**
     * Personel panelindeki detaylı filtreleme işlemi.
     * Müşteri adı, oda, tarih aralığı gibi kriterlere göre dinamik SQL oluşturur.
     */
    public Vector<Vector<Object>> getReservationsFiltered(String customerName, String roomQuery, String startDate, String endDate) {
        Vector<Vector<Object>> data = new Vector<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            StringBuilder sql = new StringBuilder(
                    "SELECT res.id, u.first_name, u.last_name, res.room_number, res.total_price, res.start_date, res.end_date, res.status " +
                            "FROM reservations res JOIN users u ON res.user_id = u.user_id JOIN rooms rm ON res.room_number = rm.room_number WHERE 1=1 "
            );
            // Dinamik sorgu oluşturma (StringBuilder ile)
            if (customerName != null && !customerName.isEmpty()) sql.append("AND (u.first_name LIKE '%").append(customerName).append("%' OR u.last_name LIKE '%").append(customerName).append("%') ");
            if (roomQuery != null && !roomQuery.isEmpty()) sql.append("AND (res.room_number LIKE '%").append(roomQuery).append("%' OR rm.room_type LIKE '%").append(roomQuery).append("%') ");
            if (startDate != null && !startDate.isEmpty()) sql.append("AND res.start_date >= '").append(startDate).append("' ");
            if (endDate != null && !endDate.isEmpty()) sql.append("AND res.start_date <= '").append(endDate).append("' ");

            sql.append("ORDER BY res.start_date DESC");
            Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql.toString());
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id")); row.add(rs.getString("first_name") + " " + rs.getString("last_name"));
                row.add(rs.getString("room_number")); row.add(rs.getString("start_date")); row.add(rs.getString("end_date"));
                row.add(rs.getDouble("total_price") + " TL"); row.add(rs.getString("status"));
                data.add(row);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return data;
    }

    // --- 9. DİĞER İŞLEMLER ---
    /**
     * Müşterinin kendi geçmiş rezervasyonlarını listeler.
     */
    public Vector<Vector<Object>> getCustomerReservations(int userId) {
        Vector<Vector<Object>> data = new Vector<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            String sql = "SELECT res.id, res.room_number, res.start_date, res.end_date, res.status, res.total_price FROM reservations res WHERE res.user_id = ? ORDER BY res.start_date DESC";
            PreparedStatement ps = conn.prepareStatement(sql); ps.setInt(1, userId); ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id")); row.add(rs.getString("room_number")); row.add(rs.getString("start_date"));
                row.add(rs.getString("end_date")); row.add(rs.getDouble("total_price")); row.add(rs.getString("status"));
                data.add(row);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return data;
    }

    /**
     * Rezervasyon iptal işlemi.
     * Rezervasyon durumunu 'İPTAL' yapar ve odayı 'MÜSAİT' durumuna getirir.
     */
    public boolean cancelReservation(int resId, String roomNo) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            String sqlRes = "UPDATE reservations SET status = 'İPTAL' WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sqlRes); ps.setInt(1, resId); ps.executeUpdate();
            String sqlRoom = "UPDATE rooms SET status = 'MÜSAİT' WHERE room_number = ?";
            PreparedStatement ps2 = conn.prepareStatement(sqlRoom); ps2.setString(1, roomNo); ps2.executeUpdate();
            return true;
        } catch (Exception e) { return false; }
    }

    /**
     * Rezervasyon durumunu günceller (Check-In / Check-Out).
     * Bu işlem aynı zamanda odanın durumunu da (DOLU / MÜSAİT) otomatik tetikler (State Mantığı).
     */
    public void updateReservationStatus(int resId, String newStatus) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            String sql = "UPDATE reservations SET status = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql); ps.setString(1, newStatus); ps.setInt(2, resId); ps.executeUpdate();

            // Rezervasyona bağlı odayı bul ve durumunu güncelle
            String sqlFindRoom = "SELECT room_number FROM reservations WHERE id = ?";
            PreparedStatement psFind = conn.prepareStatement(sqlFindRoom); psFind.setInt(1, resId); ResultSet rs = psFind.executeQuery();
            if (rs.next()) {
                String roomNo = rs.getString("room_number");
                String newRoomStatus = "CHECK-IN YAPILDI".equals(newStatus) ? "DOLU" : "MÜSAİT";
                String sqlRoom = "UPDATE rooms SET status = ? WHERE room_number = ?";
                PreparedStatement psRoom = conn.prepareStatement(sqlRoom); psRoom.setString(1, newRoomStatus); psRoom.setString(2, roomNo); psRoom.executeUpdate();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Müşteri profil bilgilerini çeker.
     */
    public String[] getCustomerProfile(int userId) {
        String[] data = new String[4];
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            String sql = "SELECT first_name, last_name, email, phone FROM users WHERE user_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql); ps.setInt(1, userId); ResultSet rs = ps.executeQuery();
            if(rs.next()) { data[0]=rs.getString("first_name"); data[1]=rs.getString("last_name"); data[2]=rs.getString("email"); data[3]=rs.getString("phone"); }
        } catch (Exception e) { e.printStackTrace(); }
        return data;
    }

    /**
     * Müşteri profilini günceller.
     */
    public boolean updateCustomerProfile(int userId, String email, String phone, String pass) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            String sql = "UPDATE users SET email=?, phone=?, password_hash=? WHERE user_id=?";
            PreparedStatement ps = conn.prepareStatement(sql); ps.setString(1, email); ps.setString(2, phone); ps.setString(3, pass); ps.setInt(4, userId); return ps.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }
}