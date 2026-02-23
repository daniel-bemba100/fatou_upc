package com.hotelmanager.model;

/**
 * Room entity representing hotel rooms
 */
public class Room extends BaseEntity {
    private String roomNumber;
    private int floor;
    private RoomType roomType;
    private RoomStatus status;
    private String description;
    private double price;

    public Room() {
        super();
        this.status = RoomStatus.AVAILABLE;
    }

    public Room(int id) {
        super(id);
        this.status = RoomStatus.AVAILABLE;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public void setRoomTypeId(int roomTypeId) {
        if (this.roomType == null) {
            this.roomType = new RoomType();
        }
        this.roomType.setId(roomTypeId);
    }

    public int getRoomTypeId() {
        return roomType != null ? roomType.getId() : 0;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public void setStatusCode(String statusCode) {
        this.status = RoomStatus.fromCode(statusCode);
    }

    public String getStatusCode() {
        return status != null ? status.getCode() : "AVAILABLE";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Room " + roomNumber + " (" + (roomType != null ? roomType.getTypeName() : "N/A") + ")";
    }
}
