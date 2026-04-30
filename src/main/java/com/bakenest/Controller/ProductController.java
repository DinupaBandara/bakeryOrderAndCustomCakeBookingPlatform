package com.bakenest.Controller;

import com.bakenest.Model.Product;
import com.bakenest.Repository.ProductRepository;
import com.bakenest.Service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    // New Toggle Method
    @PostMapping("/toggle-availability/{id}")
    public String toggleAvailability(@PathVariable Long id) {
        productRepository.findById(id).ifPresent(product -> {
            product.setAvailable(!product.isAvailable()); // Flip the boolean
            productRepository.save(product);
        });
        return "redirect:/admin/product";
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return "redirect:/admin/product";
    }

    @PostMapping("/add-product")
    public String addProduct(@ModelAttribute Product product, RedirectAttributes redirectAttributes) {
        try {
            // Delegate the complex logic to the service
            productService.registerProduct(product);
            redirectAttributes.addFlashAttribute("success", "Product added successfully!");
        } catch (Exception e) {
            // Catch the specific error message from the service
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/product";
    }
}