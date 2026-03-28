package com.scheduler.model;

public class Room {
    private int roomId;
    private String roomName;
    private int capacity;
    private boolean hasProjector;
    private boolean hasVideoConf;

    // Constructor
    public Room(int roomId, String roomName, int capacity, boolean hasProjector, boolean hasVideoConf) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.capacity = capacity;
        this.hasProjector = hasProjector;
        this.hasVideoConf = hasVideoConf;
    }

    // Getters
    public int getRoomId() { return roomId; }
    public String getRoomName() { return roomName; }
    public int getCapacity() { return capacity; }
    public boolean hasProjector() { return hasProjector; }
    public boolean hasVideoConf() { return hasVideoConf; }

    // Setters
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setHasProjector(boolean hasProjector) { this.hasProjector = hasProjector; }
    public void setHasVideoConf(boolean hasVideoConf) { this.hasVideoConf = hasVideoConf; }
    
    // Override toString for easy printing later
    @Override
    public String toString() {
        return "Room [" + roomId + "] - " + roomName + " (Capacity: " + capacity + 
               ", Projector: " + hasProjector + ", VideoConf: " + hasVideoConf + ")";
    }
}
