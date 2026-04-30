package com.bakenest.Repository;

import com.bakenest.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Custom query to find products by category for the filter buttons
    List<Product> findByCategory(String category);
}