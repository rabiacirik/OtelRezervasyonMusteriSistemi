package model;

public class FamilyRoom extends Room {
    public FamilyRoom(String roomNumber) {
        super(roomNumber, 1500.0); // Aile odası varsayılan fiyatı
    }

    @Override
    public String getRoomType() {
        return "Aile";
    }
}