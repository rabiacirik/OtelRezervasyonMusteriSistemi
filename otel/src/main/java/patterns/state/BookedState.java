package patterns.state;
import model.Room;

public class BookedState implements RoomState {
    @Override
    public void handleRequest(Room room) {
        System.out.println("Müşteri giriş yaptı. Oda doldu.");
        room.setState(new OccupiedState());
    }
    @Override public String getStatusName() { return "REZERVE"; }
}