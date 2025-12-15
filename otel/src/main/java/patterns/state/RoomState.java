package patterns.state;
import model.Room;

public interface RoomState {
    void handleRequest(Room room);
    String getStatusName();
}