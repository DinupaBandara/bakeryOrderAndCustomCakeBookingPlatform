package com.bakenest.Repository;

import com.bakenest.Model.CartItem;
import com.bakenest.Model.Customer;
import com.bakenest.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCustomer(Customer customer);
    Optional<CartItem> findByCustomerAndProduct(Customer customer, Product product);
    void deleteByCustomer(Customer customer);
    List<CartItem> getCartItemsByCustomer(Customer customer);

}