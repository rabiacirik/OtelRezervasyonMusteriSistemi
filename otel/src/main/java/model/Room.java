package model;

import patterns.state.RoomState;
import patterns.state.AvailableState;

//  Abstract Class
public abstract class Room {
    protected String roomNumber;
    protected double basePrice;
    protected RoomState state; // State Deseni burada devreye giriyor

    public Room(String roomNumber, double basePrice) {
        this.roomNumber = roomNumber;
        this.basePrice = basePrice;
        this.state = new AvailableState(); // Varsayılan durum
    }

    public void setState(RoomState state) {
        this.state = state;
    }

    public RoomState getState() {
        return state;
    }

    public abstract String getRoomType();

    public double getPrice() { return basePrice; }
    public String getRoomNumber() { return roomNumber; }
}