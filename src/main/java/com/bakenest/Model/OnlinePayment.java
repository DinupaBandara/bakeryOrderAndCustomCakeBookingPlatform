package com.bakery.billing.model;

public class OnlinePayment extends Payment {

    private String transactionReference;
    private String cardType;
    private double serviceFee;

    public OnlinePayment(String paymentId, double amount, String paymentDate,
                         String transactionReference, String cardType) {
        super(paymentId, amount, paymentDate);
        this.transactionReference = transactionReference;
        this.cardType = cardType;
        this.serviceFee = 50.00;
    }

    @Override
    public String getPaymentMethod() {
        return "Online (" + cardType + ")";
    }

    @Override
    public double calculateTotal() {
        return getAmount() + serviceFee;
    }

    @Override
    public String processPayment() {
        setStatus("PAID");
        return "Online payment via " + cardType +
               " confirmed. Ref: " + transactionReference +
               " | Total charged: Rs. " + calculateTotal();
    }

    public String getTransactionReference() { return transactionReference; }
    public void setTransactionReference(String ref) { this.transactionReference = ref; }

    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }

    public double getServiceFee() { return serviceFee; }
    public void setServiceFee(double fee) { this.serviceFee = fee; }

    @Override
    public String toString() {
        return super.toString() +
               " | Ref: " + transactionReference +
               " | Service Fee: Rs. " + serviceFee +
               " | Total: Rs. " + calculateTotal();
    }
}
