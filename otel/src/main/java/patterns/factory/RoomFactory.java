package patterns.factory;

import model.Room;
import model.StandardRoom;
import model.SuiteRoom;
import model.FamilyRoom;

public class RoomFactory {
    public static Room createRoom(String type, String roomNumber) {
        if (type.equalsIgnoreCase("Standart")) {
            return new StandardRoom(roomNumber);
        } else if (type.equalsIgnoreCase("Suit")) {
            return new SuiteRoom(roomNumber);
        } else if (type.equalsIgnoreCase("Aile")) {
            return new FamilyRoom(roomNumber);
        }
        return null;
    }
}