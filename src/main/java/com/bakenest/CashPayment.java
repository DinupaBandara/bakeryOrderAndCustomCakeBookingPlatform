package com.bakery.billing.model;

public class CashPayment extends Payment {

    private double amountGiven;
    private double changeReturned;

    public CashPayment(String paymentId, double amount, String paymentDate, double amountGiven) {
        super(paymentId, amount, paymentDate);
        this.amountGiven = amountGiven;
        this.changeReturned = calculateChange();
    }

    @Override
    public String getPaymentMethod() {
        return "Cash";
    }

    @Override
    public double calculateTotal() {
        return getAmount();
    }

    public double calculateChange() {
        double change = amountGiven - getAmount();
        return change >= 0 ? change : 0;
    }

    @Override
    public String processPayment() {
        if (amountGiven < getAmount()) {
            return "Insufficient cash! Amount due: Rs. " + getAmount() +
                   " | Amount given: Rs. " + amountGiven;
        }
        setStatus("PAID");
        this.changeReturned = calculateChange();
        return "Cash payment received. Change returned: Rs. " + changeReturned;
    }

    public double getAmountGiven() { return amountGiven; }
    public void setAmountGiven(double amountGiven) {
        this.amountGiven = amountGiven;
        this.changeReturned = calculateChange();
    }

    public double getChangeReturned() { return changeReturned; }

    @Override
    public String toString() {
        return super.toString() +
               " | Cash Given: Rs. " + amountGiven +
               " | Change: Rs. " + changeReturned;
    }
}
