package com.bakenest.Service;

import com.bakenest.Model.CartItem;
import com.bakenest.Model.Customer;
import com.bakenest.Model.Product;
import com.bakenest.Repository.CartRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    public void addToCart(Customer customer, Product product, int quantity) {
        Optional<CartItem> existingItem = cartRepository.findByCustomerAndProduct(customer, product);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCustomer(customer);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cartRepository.save(newItem);
        }
    }

    @Transactional
    public void addCustomCakeToCart(CartItem cartItem) {
        // Ensure the price is calculated based on your logic before saving
        if (cartItem.getCustomCake() != null) {
            cartItem.getCustomCake().calculateAndSetPrice();
        }
        cartRepository.save(cartItem);
    }

    public List<CartItem> getCartItems(Customer customer) {
        return cartRepository.findByCustomer(customer);
    }

    public void removeFromCart(Long cartItemId) {
        cartRepository.deleteById(cartItemId);
    }

    public double calculateTotal(Customer customer) {
        return cartRepository.findByCustomer(customer).stream()
                .mapToDouble(CartItem::getTotalPrice) // This now calls our fixed helper method
                .sum();
    }

    @Transactional
    public void clearCart(Customer customer) {
        // Delete all cart items for the specific customer
        cartRepository.deleteByCustomer(customer);
    }

    @Transactional
    public void updateQuantity(Long cartItemId, int newQuantity) {
        // Find the specific item in the database
        CartItem item = cartRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // Update the quantity and save it
        item.setQuantity(newQuantity);
        cartRepository.save(item);
    }
}