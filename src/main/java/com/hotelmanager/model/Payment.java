package com.hotelmanager.model;

import java.time.LocalDateTime;

/**
 * Payment entity representing payment transactions
 */
public class Payment extends BaseEntity {
    private int reservationId;
    private String paymentMethod;
    private double amount;
    private PaymentStatus paymentStatus;
    private String transactionId;
    private LocalDateTime paymentDate;
    private String notes;

    public Payment() {
        super();
        this.paymentStatus = PaymentStatus.PENDING;
        this.paymentDate = LocalDateTime.now();
    }

    public Payment(int id) {
        super(id);
        this.paymentStatus = PaymentStatus.PENDING;
        this.paymentDate = LocalDateTime.now();
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setPaymentStatusCode(String statusCode) {
        this.paymentStatus = PaymentStatus.fromCode(statusCode);
    }

    public String getPaymentStatusCode() {
        return paymentStatus != null ? paymentStatus.getCode() : "PENDING";
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Payment #" + id + " - $" + amount + " (" + (paymentStatus != null ? paymentStatus.getDisplayName() : "N/A") + ")";
    }
}
