package com.bakenest.Controller;

import com.bakenest.Model.Customer;
import com.bakenest.Model.Product;
import com.bakenest.Repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@Controller
public class ViewController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/home")
    public String showHomePage(Model model) {
        model.addAttribute("backendMessage", "Hello! This data came from the Spring Backend.");
        return "index";
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        // THIS IS THE FIX: Pass an empty customer object for the register form
        model.addAttribute("customer", new Customer());
        return "login";
    }

    @GetMapping("/customer/product")
    public String showProductPage(Model model) {
        return "/customer/product";
    }

    @GetMapping("/customer/product/customcake")
    public String showCustomCakePage(Model model) {
        return "/customer/customeCake";
    }

    @GetMapping("/customer/product/bakeryitems")
    public String showBackeryItemPage(Model model) {
        return "/customer/bakeryItem";
    }

    @GetMapping("/admin/dashboard")
    public String showAdminDashboardPage(HttpSession session) {
        // Check if the user is actually logged in
        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/login"; // Send them back to login if session died
        }
        return "admin/dashboard";
    }

    @GetMapping("/admin/product")
    public String showAdminProductPage(Model model, HttpSession session) {
        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/login";
        }

        List<Product> productList = productRepository.findAll();
        model.addAttribute("products", productList);

        return "admin/product";
    }
}