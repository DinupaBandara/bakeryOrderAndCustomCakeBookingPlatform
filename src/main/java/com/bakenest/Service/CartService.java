package com.bakenest.Service;

import com.bakenest.Model.CartItem;
import com.bakenest.Model.Customer;
import com.bakenest.Model.Product;
import com.bakenest.Repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<CartItem> getCartItems(Customer customer) {
        return cartRepository.findByCustomer(customer);
    }

    public void removeFromCart(Long cartItemId) {
        cartRepository.deleteById(cartItemId);
    }

    public double calculateTotal(Customer customer) {
        return cartRepository.findByCustomer(customer).stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
    }
}