package com.bakenest.model;

public class OnlinePayment extends Payment {

    private String cardType;

    public OnlinePayment(String paymentId, double amount, String cardType) {
        super(paymentId, amount);
        this.cardType = cardType;
    }

    @Override
    public String getPaymentMethod() {
        return "Online (" + cardType + ")";
    }

    @Override
    public double calculateTotal() {
        return amount;
    }

    @Override
    public String processPayment() {
        if ("PAID".equals(status)) {
            return "Already processed.";
        }

        status = "PAID";
        return "Online payment of RS. " + amount +" is successfull.";
               
    }

    @Override
    public String toString() {
        return super.toString() +
               "\n Card Type: " + cardType;
    }
}
