package com.bakenest.Service;

import com.bakenest.Model.*;
import com.bakenest.Repository.BillRepository;
import com.bakenest.Repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BillingService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    public Bill generateBill(Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) 
            return null;

        Bill bill = new Bill();
        bill.setOrder(order);
        bill.setSubtotal(order.getTotalAmount());
        bill.setDiscount(0);
        bill.setPaymentStatus("PENDING");

        return billRepository.save(bill);
    }

    @Transactional
    public Bill processPayment(Long orderId, String type, double discount, String cardType, double amountGiven) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null)
            return null;

        Bill bill = billRepository.findByOrder(order).orElse(null);
        if (bill == null) 
            return null;

        Payment payment = createPayment(type, bill.getSubtotal(), cardType, amountGiven);
        String paymentResult = payment.processPayment(); 
        
        if (paymentResult.contains("Insufficient cash")) {
            return null; 
        }

        bill.setDiscount(validDiscount(discount));
        bill.setPaymentMethod(type);
        bill.setPaymentStatus("PAID");

        return billRepository.save(bill);
    }

    private Payment createPayment(String type, double amount, String cardType, double amountGiven) {
        if ("ONLINE".equalsIgnoreCase(type)) {
            return new OnlinePayment(type, amount, cardType); 
        }
        return new CashPayment(type, amount, amountGiven); 
    }

    private double validDiscount(double discount) {
        if (discount < 0 || discount > 100) {
            return 0;
        }
        return discount;
    }
}
