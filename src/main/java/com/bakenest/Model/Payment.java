package com.bakery.billing.model;

public abstract class Payment {

    private String paymentId;
    private double amount;
    private String paymentDate;
    private String status;

    public Payment(String paymentId, double amount, String paymentDate) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.status = "PENDING";
    }

    public abstract String getPaymentMethod();

    public abstract double calculateTotal();

    public String processPayment() {
        this.status = "PAID";
        return "Payment of Rs. " + amount + " processed successfully.";
    }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String id) { this.paymentId = id; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getPaymentDate() { return paymentDate; }
    public void setPaymentDate(String date) { this.paymentDate = date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "PaymentID: " + paymentId +
               " | Method: " + getPaymentMethod() +
               " | Amount: Rs. " + amount +
               " | Status: " + status +
               " | Date: " + paymentDate;
    }
}
