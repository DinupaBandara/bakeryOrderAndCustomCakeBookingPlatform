package com.bakenest.Model;

public class CashPayment extends Payment {

    private double amountGiven;
    private double change;

    public CashPayment(String paymentId, double amount, double amountGiven) {
        super(paymentId, amount);
        this.amountGiven = amountGiven;
    }

    @Override
    public String getPaymentMethod() {
        return "Cash";
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

        if (amountGiven < amount) {
            return "Insufficient cash. Required : Rs. " + amount +
                   "\n Given : Rs. " + amountGiven;
        }

        status = "PAID";
        change = amountGiven - amount;

        return "Cash payment successful"+"\n"+"Change : Rs. " + change;
    }

    @Override
    public String toString() {
        return super.toString() +
               "\n Cash Given: Rs. " + amountGiven +
               "\n Change: Rs. " + change;
    }
}
