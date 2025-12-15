package model;

public class StandardRoom extends Room {
    public StandardRoom(String roomNumber) {
        super(roomNumber, 1000.0); // Standart fiyat
    }

    @Override
    public String getRoomType() {
        return "Standart";
    }
}