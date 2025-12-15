package model;

import java.sql.Date;

public class Reservation {
    private int id;
    private int userId;
    private String roomNumber;
    private Date startDate;
    private Date endDate;
    private String status; // Beklemede, Onaylandı, İptal

    public Reservation(int userId, String roomNumber, Date startDate, Date endDate, String status) {
        this.userId = userId;
        this.roomNumber = roomNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    // Getter Metotları (Listeleme için lazım)
    public String getRoomNumber() { return roomNumber; }
    public Date getStartDate() { return startDate; }
    public Date getEndDate() { return endDate; }
    public String getStatus() { return status; }
}