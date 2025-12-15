package patterns.state;
import model.Room;

public class AvailableState implements RoomState {
    @Override
    public void handleRequest(Room room) {
        System.out.println("Oda rezerve edildi.");
        room.setState(new BookedState());
    }
    @Override public String getStatusName() { return "MÜSAİT"; }
}