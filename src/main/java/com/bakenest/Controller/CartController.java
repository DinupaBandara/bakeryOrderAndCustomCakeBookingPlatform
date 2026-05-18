package com.bakenest.Controller;

import com.bakenest.Model.CartItem;
import com.bakenest.Model.Customer;
import com.bakenest.Model.Product;
import com.bakenest.Repository.CartRepository;
import com.bakenest.Model.CustomCake;
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
    @Autowired
    private CartRepository cartRepository;

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam int quantity,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {

        // 1. Retrieve and validate session from user role
        Customer customer = (Customer) session.getAttribute("loggedUser");
        String role = (String) session.getAttribute("role");

        if (customer == null || !"CUSTOMER".equals(role)) {
            // Passes message to th:if="${error}" in your fragment
            redirectAttributes.addFlashAttribute("error", "Please sign in to add items to your cart.");
            return "redirect:/login";
        }

        //  Validate quantity (must be greater than 0)
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

    @DeleteMapping("/remove/{id}")
    @ResponseBody
    public ResponseEntity<String> ajaxRemoveItem(@PathVariable Long id) {
        try {
            cartService.removeFromCart(id);
            return ResponseEntity.ok("Removed");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error removing item");
        }
    }

    @PostMapping("/update/{id}")
    @ResponseBody // <-- CRITICAL: This tells Spring NOT to look for an HTML page
    public ResponseEntity<String> updateQuantity(@PathVariable Long id, @RequestParam int quantity) {
        try {
            // Call the service method just we created
            cartService.updateQuantity(id, quantity);
            return ResponseEntity.ok("Quantity updated in database");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating quantity");
        }
    }

    @PostMapping("/add-custom")
    public String addCustomCakeToCart(@ModelAttribute CustomCake customCake, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedUser");
        if (customer == null) return "redirect:/login";

        // 1. Link cake to customer and calculate price using your logic
        customCake.setCustomer(customer);
        customCake.calculateAndSetPrice(); // The logic we wrote earlier

        // 2. Create cart item wrapper
        CartItem cartItem = new CartItem();
        cartItem.setCustomer(customer);
        cartItem.setCustomCake(customCake); // Link the custom cake
        cartItem.setQuantity(1);

        // 3. Save via service (Ensure service handles  Cascading)
        cartRepository.save(cartItem);

        return "redirect:/customer/cart";
    }
}