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
    public String toggleAvailability(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productRepository.findById(id).ifPresent(product -> {
            product.setAvailable(!product.isAvailable()); // Flip the boolean
            productRepository.save(product);
            redirectAttributes.addFlashAttribute("success", "Availability Changed Successfully");
        });
        return "redirect:/admin/product";
    }

//    @PostMapping("/delete/{id}")
//    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
//        productRepository.deleteById(id);
//        redirectAttributes.addFlashAttribute("success", "Product deleted successfully!");
//        return "redirect:/admin/product";
//    }

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

    @PostMapping("/update-product")
    public String updateProduct(@ModelAttribute Product product, RedirectAttributes redirectAttributes) {
        try {
            productService.updateProduct(product);
            redirectAttributes.addFlashAttribute("success", "Product updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/product";
    }
}