package model;

public class SuiteRoom extends Room {
    public SuiteRoom(String roomNumber) {
        super(roomNumber, 2500.0); // Suit fiyat
    }

    @Override
    public String getRoomType() {
        return "Suit";
    }
}