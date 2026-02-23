package com.hotelmanager.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Reservation entity representing hotel bookings
 */
public class Reservation extends BaseEntity {
    private Customer customer;
    private Room room;
    private User user;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numberOfGuests;
    private double totalAmount;
    private ReservationStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Reservation() {
        super();
        this.status = ReservationStatus.PENDING;
        this.numberOfGuests = 1;
    }

    public Reservation(int id) {
        super(id);
        this.status = ReservationStatus.PENDING;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setCustomerId(int customerId) {
        if (this.customer == null) {
            this.customer = new Customer();
        }
        this.customer.setId(customerId);
    }

    public int getCustomerId() {
        return customer != null ? customer.getId() : 0;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public void setRoomId(int roomId) {
        if (this.room == null) {
            this.room = new Room();
        }
        this.room.setId(roomId);
    }

    public int getRoomId() {
        return room != null ? room.getId() : 0;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setUserId(int userId) {
        if (this.user == null) {
            this.user = new User();
        }
        this.user.setId(userId);
    }

    public int getUserId() {
        return user != null ? user.getId() : 0;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public void setStatusCode(String statusCode) {
        this.status = ReservationStatus.fromCode(statusCode);
    }

    public String getStatusCode() {
        return status != null ? status.getCode() : "PENDING";
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public long getNumberOfNights() {
        if (checkInDate != null && checkOutDate != null) {
            return java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Reservation #" + id + " - " + (customer != null ? customer.getFullName() : "N/A");
    }
}
