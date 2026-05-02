package com.bakenest.Controller;

import com.bakenest.Model.Customer;
import com.bakenest.Model.Product;
import com.bakenest.Repository.ProductRepository;
import com.bakenest.Service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customer/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductRepository productRepository;

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam int quantity,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {

        // 1. Retrieve and validate session user and role
        Customer customer = (Customer) session.getAttribute("loggedUser");
        String role = (String) session.getAttribute("role");

        if (customer == null || !"CUSTOMER".equals(role)) {
            // Passes message to th:if="${error}" in your fragment
            redirectAttributes.addFlashAttribute("error", "Please sign in to add items to your cart.");
            return "redirect:/login";
        }

        // 2. Validate quantity (ensure it's at least 1)
        if (quantity < 1) {
            redirectAttributes.addFlashAttribute("error", "Invalid quantity selected.");
            return "redirect:/customer/product/bakeryitems";
        }

        // 3. Find product and add to cart
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            try {
                cartService.addToCart(customer, product, quantity);

                // Passes message to th:if="${success}" in your fragment
                redirectAttributes.addFlashAttribute("success",
                        quantity + " " + product.getName() + "(s) added to cart!");

            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Failed to update cart. Please try again.");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "The selected product could not be found.");
        }

        // 4. Redirect back to the shop to trigger the notification script
        return "redirect:/customer/product/bakeryitems";
    }

    @GetMapping("/remove/{id}")
    public String removeItem(@PathVariable Long id) {
        cartService.removeFromCart(id);
        return "redirect:/customer/cart";
    }
}