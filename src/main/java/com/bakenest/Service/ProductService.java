package com.bakenest.Service;

import com.bakenest.Model.Product;
import com.bakenest.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public void registerProduct(Product product) throws Exception {
        // 1. Check if name already exists
        if (!productRepository.existsByName(product.getName())) {
            throw new Exception("A product with this name already exists!");
        }

        // 2. Check if Image URL already exists
        if (productRepository.existsByImageUrl(product.getImageUrl())) {
            throw new Exception("This image URL is already in use by another product.");
        }

        // 3. Set default state and save
        product.setAvailable(true);
        productRepository.save(product);
    }

    public void updateProduct(Product product) throws Exception {
        Product existing = productRepository.findById(product.getId())
                .orElseThrow(() -> new Exception("Product not found"));

        // Check name uniqueness only if name is changed
        if (!existing.getName().equals(product.getName()) &&
                productRepository.existsByName(product.getName())) {
            throw new Exception("Another product already has this name!");
        }

        // Update fields
        existing.setName(product.getName());
        existing.setCategory(product.getCategory());
        existing.setPrice(product.getPrice());
        existing.setImageUrl(product.getImageUrl());
        existing.setDescription(product.getDescription());

        productRepository.save(existing);
    }
}