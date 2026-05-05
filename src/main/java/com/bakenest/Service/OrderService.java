package com.bakenest.Service;

import com.bakenest.Model.*;
import com.bakenest.Repository.CartRepository;
import com.bakenest.Repository.CustomerRepository;
import com.bakenest.Repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CustomerRepository customerRepository; // Added this!

    @Transactional
    public Order placeOrder(Customer sessionCustomer, String address, String city, String zipCode, String paymentMethod) {

        // 1. Fetch managed customer to ensure we can safely update them
        Customer customer = customerRepository.findById(sessionCustomer.getId()).orElse(sessionCustomer);

        // 2. Profile Update Logic: If they have no address, save the new one!
        boolean profileUpdated = false;
        if (customer.getAddress() == null || customer.getAddress().isEmpty()) {
            customer.setAddress(address);
            profileUpdated = true;
        }
        if (customer.getCity() == null || customer.getCity().isEmpty()) {
            customer.setCity(city);
            profileUpdated = true;
        }
        if (customer.getZipCode() == null || customer.getZipCode().isEmpty()) {
            customer.setZipCode(zipCode);
            profileUpdated = true;
        }

        if (profileUpdated) {
            customerRepository.save(customer);
        }

        // 3. Get current cart items
        List<CartItem> cartItems = cartRepository.findByCustomer(customer);
        if (cartItems.isEmpty()) return null;

        // 4. Calculate subtotal
        double subtotal = cartItems.stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();

        // 5. Create the Order object and apply the checkout details
        Order order = new Order();
        order.setCustomer(customer);
        order.setDeliveryAddress(address);
        order.setCity(city);
        order.setZipCode(zipCode);
        order.setPaymentMethod(paymentMethod);
        order.setSubtotal(subtotal);
        order.setDeliveryFee(this.currentDeliveryFee);
        order.setPackagingFee(this.currentPackagingFee);
        order.setStatus("PENDING");

        // 6. Convert CartItems to OrderItems (Snapshotting prices)
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setQuantity(cartItem.getQuantity());

            if (cartItem.getProduct() != null) {
                orderItem.setProduct(cartItem.getProduct());
                orderItem.setPriceAtOrder(cartItem.getProduct().getPrice());
            } else if (cartItem.getCustomCake() != null) {
                // Custom Cake Flow snapshot
                CustomCake cakeSnapshot = new CustomCake();
                CustomCake original = cartItem.getCustomCake();

                cakeSnapshot.setWeight(original.getWeight());
                cakeSnapshot.setFlavorType(original.getFlavorType());
                cakeSnapshot.setCakeType(original.getCakeType());
                cakeSnapshot.setEventType(original.getEventType());
                cakeSnapshot.setInscription(original.getInscription());
                cakeSnapshot.setPrice(original.getPrice());
                cakeSnapshot.setCustomer(customer);

                orderItem.setCustomCake(cakeSnapshot);
                orderItem.setPriceAtOrder(original.getPrice());
            }
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);

        // 7. Save order and clear the cart
        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(customer);

        return savedOrder;
    }

    @Transactional
    public boolean cancelOrder(Long orderId, Customer customer) {
        // Find the order by ID
        Order order = orderRepository.findById(orderId).orElse(null);

        // Security Check: Make sure the order exists AND belongs to the logged-in user
        if (order != null && order.getCustomer().getId().equals(customer.getId())) {

            // Double check that it is still in a cancellable state
            if (order.getStatus().equals("PENDING") || order.getStatus().equals("CONFIRMED")) {
                order.setStatus("CANCELLED");
                orderRepository.save(order);
                return true;
            }
        }
        return false;
    }

    // 1. Store the global fees here centrally
    private double currentDeliveryFee = 50.00;
    private double currentPackagingFee = 20.00;

    public double getDeliveryFee() { return currentDeliveryFee; }
    public double getPackagingFee() { return currentPackagingFee; }

    // Admin method to update fees
    public void updateGlobalFees(double delivery, double packaging) {
        this.currentDeliveryFee = delivery;
        this.currentPackagingFee = packaging;
    }
}