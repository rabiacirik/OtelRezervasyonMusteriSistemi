package patterns.state;
import model.Room;

public class OccupiedState implements RoomState {
    @Override
    public void handleRequest(Room room) {
        System.out.println("Müşteri çıktı. Oda temizlendi.");
        room.setState(new AvailableState());
    }
    @Override public String getStatusName() { return "DOLU"; }
}