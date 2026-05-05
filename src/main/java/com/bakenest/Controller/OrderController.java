package com.bakenest.Controller;

import com.bakenest.Model.Customer;
import com.bakenest.Model.Order;
import com.bakenest.Repository.CartRepository;
import com.bakenest.Repository.OrderRepository;
import com.bakenest.Service.CartService;
import com.bakenest.Service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customer/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderRepository orderRepository;

    // Notice we mapped this to "/place" to match your HTML form action!
    @PostMapping("/place")
    public String placeOrder(
            @RequestParam String deliveryAddress,
            @RequestParam String city,
            @RequestParam(required = false) String zipCode,
            @RequestParam String paymentMethod,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // 1. Check authentication
        Customer customer = (Customer) session.getAttribute("loggedUser");
        String role = (String) session.getAttribute("role");

        if (customer == null || !"CUSTOMER".equals(role)) {
            return "redirect:/login";
        }

        try {
            // 2. Place the order with all the form details
            Order order = orderService.placeOrder(customer, deliveryAddress, city, zipCode, paymentMethod);

            if (order != null) {
                // 3. Update the session so the UI immediately knows about the new address!
                if (customer.getAddress() == null || customer.getAddress().isEmpty()) {
                    customer.setAddress(deliveryAddress);
                    customer.setCity(city);
                    customer.setZipCode(zipCode);
                    session.setAttribute("loggedUser", customer);
                }

                redirectAttributes.addFlashAttribute("success",
                        "Order #" + order.getId() + " placed successfully!");

                // Redirect to a dedicated Success Page or back to products
                return "redirect:/customer/product";
            } else {
                redirectAttributes.addFlashAttribute("error", "Your cart is empty!");
                return "redirect:/customer/cart";
            }
        } catch (Exception e) {
            System.err.println("Checkout Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Transaction failed. Please check your details and try again.");
            return "redirect:/customer/order/checkout"; // Send them back to checkout on fail
        }
    }

    @PostMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<String> updateQuantity(@PathVariable Long id, @RequestParam int quantity) {
        try {
            cartService.updateQuantity(id, quantity);
            return ResponseEntity.ok("Updated");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating quantity");
        }
    }

    @PostMapping("/cancel/{id}")
    public String cancelOrder(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {

        // 1. Check Authentication
        Customer customer = (Customer) session.getAttribute("loggedUser");
        if (customer == null) {
            return "redirect:/login";
        }

        // 2. Attempt to cancel the order
        boolean success = orderService.cancelOrder(id, customer);

        // 3. Send notification back to the UI
        if (success) {
            redirectAttributes.addFlashAttribute("success", "Order #BN-" + id + " has been cancelled successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Order could not be cancelled. It may have already started preparation.");
        }

        // Refresh the orders page
        return "redirect:/customer/orders";
    }

    @PostMapping("/update-fees")
    public String updateGlobalFees(@RequestParam double deliveryFee, @RequestParam double packagingFee, RedirectAttributes redirectAttributes) {

        // Send the updated fees to the central service!
        orderService.updateGlobalFees(deliveryFee, packagingFee);

        redirectAttributes.addFlashAttribute("success", "Global store fees updated successfully.");
        return "redirect:/admin/orders";
    }

    @PostMapping("/update-status")
    public String updateOrderStatus(
            @RequestParam Long orderId,
            @RequestParam String status,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // Security Check
        if (!"ADMIN".equals(session.getAttribute("role"))) return "redirect:/login";

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(status);
            orderRepository.save(order);
            redirectAttributes.addFlashAttribute("success", "Order #BN-" + orderId + " has been updated to " + status + ".");
        } else {
            redirectAttributes.addFlashAttribute("error", "Order not found.");
        }

        return "redirect:/admin/orders";
    }
}