package com.bakery.billing.model;

public class Bill {

    private String billId;
    private String orderId;
    private String customerName;
    private String customerPhone;
    private String orderItems;
    private double subtotal;
    private double discount;
    private double totalAmount;
    private String paymentMethod;
    private String paymentStatus;
    private String createdDate;

    public Bill(String billId, String orderId, String customerName,
                String customerPhone, String orderItems,
                double subtotal, double discount, String paymentMethod,
                String paymentStatus, String createdDate) {
        this.billId = billId;
        this.orderId = orderId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.orderItems = orderItems;
        this.subtotal = subtotal;
        this.discount = discount;
        this.totalAmount = subtotal - discount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.createdDate = createdDate;
    }

    public void recalculateTotal() {
        this.totalAmount = subtotal - discount;
    }

    public String toFileString() {
        return billId + "|" + orderId + "|" + customerName + "|" +
               customerPhone + "|" + orderItems + "|" + subtotal + "|" +
               discount + "|" + totalAmount + "|" + paymentMethod + "|" +
               paymentStatus + "|" + createdDate;
    }

    public static Bill fromFileString(String line) {
        String[] parts = line.split("\\|");
        return new Bill(
            parts[0],
            parts[1],
            parts[2],
            parts[3],
            parts[4],
            Double.parseDouble(parts[5]),
            Double.parseDouble(parts[6]),
            parts[8],
            parts[9],
            parts[10]
        );
    }

    public String getBillId() { return billId; }
    public void setBillId(String billId) { this.billId = billId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String name) { this.customerName = name; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String phone) { this.customerPhone = phone; }

    public String getOrderItems() { return orderItems; }
    public void setOrderItems(String items) { this.orderItems = items; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
        recalculateTotal();
    }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) {
        this.discount = discount;
        recalculateTotal();
    }

    public double getTotalAmount() { return totalAmount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String method) { this.paymentMethod = method; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String status) { this.paymentStatus = status; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String date) { this.createdDate = date; }

    @Override
    public String toString() {
        return "==============================\n" +
               "  BAKERY INVOICE\n" +
               "==============================\n" +
               "Bill ID      : " + billId + "\n" +
               "Order ID     : " + orderId + "\n" +
               "Customer     : " + customerName + "\n" +
               "Phone        : " + customerPhone + "\n" +
               "Items        : " + orderItems + "\n" +
               "------------------------------\n" +
               "Subtotal     : Rs. " + subtotal + "\n" +
               "Discount     : Rs. " + discount + "\n" +
               "Total        : Rs. " + totalAmount + "\n" +
               "Payment      : " + paymentMethod + "\n" +
               "Status       : " + paymentStatus + "\n" +
               "Date         : " + createdDate + "\n" +
               "==============================";
    }
}
