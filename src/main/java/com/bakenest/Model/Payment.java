package com.bakenest.model;

public abstract class Payment {

    protected String paymentId;
    protected double amount;
    protected String status = "PENDING";

    public Payment(String paymentId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        this.paymentId = paymentId;
        this.amount = amount;
    }

    public abstract String getPaymentMethod();

    public abstract double calculateTotal();

    public String processPayment() {
        if ("PAID".equals(status)) {
            return "Already processed.";
        }

        status = "PAID";
        return "Payment of Rs. " + amount + " is completed.";
    }

    public String getPaymentId() {
        return paymentId;
    }

    public double getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

   @Override
public String toString() {
    return "Payment ID: " + paymentId +
           "\n Method: " + getPaymentMethod() +
           "\n Amount: Rs. " + amount +
           "\n Status: " + status;
}
}
