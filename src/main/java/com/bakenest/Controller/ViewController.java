package com.bakenest.Controller;

import com.bakenest.Model.Admin;
import com.bakenest.Model.Customer;
import com.bakenest.Model.Product;
import com.bakenest.Repository.AdminRepository;
import com.bakenest.Repository.CustomerRepository;
import com.bakenest.Repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Controller
public class ViewController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AdminRepository adminRepository;

    @PostMapping("/auth/logout")
    public String logout(HttpSession session) {
        // 1. Completely clear all session data (loggedUser, role, etc.)
        session.invalidate();

        // 2. Redirect the user to the login page with a logout message
        return "redirect:/home?logout";
    }

    @GetMapping("/home")
    public String showHomePage(Model model, HttpSession session) {
        Object sessionUser = session.getAttribute("loggedUser");

        if (sessionUser instanceof Customer) {
            model.addAttribute("user", (Customer) sessionUser);
        } else {
            model.addAttribute("user", null);
        }

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
    public String showProductPage(Model model, HttpSession session) {
        Object sessionUser = session.getAttribute("loggedUser");

        if (sessionUser instanceof Customer) {
            model.addAttribute("user", (Customer) sessionUser);
        } else {
            model.addAttribute("user", null);
        }

        return "/customer/product";
    }

    @GetMapping("/customer/product/customcake")
    public String showCustomCakePage(Model model, HttpSession session) {
        Object sessionUser = session.getAttribute("loggedUser");

        if (sessionUser instanceof Customer) {
            model.addAttribute("user", (Customer) sessionUser);
        } else {
            model.addAttribute("user", null);
        }

        return "/customer/customeCake";
    }

    @GetMapping("/customer/product/bakeryitems")
    public String showBakeryItemPage(@RequestParam(required = false) String category,
                                     Model model,
                                     HttpSession session) {

        // Retrieve from session safely
        Object sessionUser = session.getAttribute("loggedUser");

        if (sessionUser instanceof Customer) {
            Customer loggedInUser = (Customer) sessionUser;
            model.addAttribute("user", loggedInUser);
        } else {
            model.addAttribute("user", null); // Shows 'Guest' in your header logic
        }

        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);

        // 3. Extract unique categories that have products
        Set<String> activeCategories = products.stream()
                .map(Product::getCategory)
                .collect(Collectors.toSet());

        model.addAttribute("activeCategories", activeCategories);

        return "/customer/bakeryItem";
    }

    @GetMapping("/customer/profile")
    public String showCustomerProfile(Model model, HttpSession session) {
        Object sessionUser = session.getAttribute("loggedUser");

        if (sessionUser instanceof Customer) {
            model.addAttribute("user", (Customer) sessionUser);
            return "/customer/profile";
        }

        return "redirect:/login"; // Redirect guests away from the profile page
    }

    // --- Admin Dashboard ---
    @GetMapping("/admin/dashboard")
    public String showAdminDashboardPage(HttpSession session, Model model) {
        Object loggedUser = session.getAttribute("loggedUser");
        String role = (String) session.getAttribute("role");

        // Check if the user is an Admin and the role matches
        if (!(loggedUser instanceof Admin) || !"ADMIN".equals(role)) {
            return "redirect:/login";
        }

        model.addAttribute("admin", loggedUser);
        model.addAttribute("activePage", "dashboard");
        return "admin/dashboard";
    }

    // --- Product Management ---
    @GetMapping("/admin/product")
    public String showAdminProductPage(Model model, HttpSession session) {
        Object loggedUser = session.getAttribute("loggedUser");
        String role = (String) session.getAttribute("role");

        if (!(loggedUser instanceof Admin) || !"ADMIN".equals(role)) {
            return "redirect:/login";
        }

        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("admin", loggedUser);
        model.addAttribute("activePage", "products");
        return "admin/product";
    }

    // --- Admin Management (Super Admin Only) ---
    @GetMapping("/admin/adminManagement")
    public String showManagementPage(Model model, HttpSession session) {
        Object loggedUser = session.getAttribute("loggedUser");
        String role = (String) session.getAttribute("role");

        if (loggedUser instanceof Admin && "ADMIN".equals(role)) {
            Admin loggedIn = (Admin) loggedUser;
            // Check for Super Admin privileges
            if (!loggedIn.isSuperAdmin()) {
                return "redirect:/admin/dashboard";
            }
            model.addAttribute("admin", loggedIn);
            model.addAttribute("admins", adminRepository.findAll());
            model.addAttribute("activePage", "management");
            return "Admin/adminManagement";
        }

        return "redirect:/login";
    }

    // --- Admin Profile ---
    @GetMapping("/admin/profile")
    public String showProfilePage(Model model, HttpSession session) {
        Object loggedUser = session.getAttribute("loggedUser");
        String role = (String) session.getAttribute("role");

        if (!(loggedUser instanceof Admin) || !"ADMIN".equals(role)) {
            return "redirect:/login";
        }

        model.addAttribute("admin", loggedUser);
        model.addAttribute("activePage", "profile");
        return "Admin/profile";
    }

    // --- Customer Management ---
    @GetMapping("/admin/customers")
    public String showCustomerPage(Model model, HttpSession session) {
        Object loggedUser = session.getAttribute("loggedUser");
        String role = (String) session.getAttribute("role");

        if (!(loggedUser instanceof Admin) || !"ADMIN".equals(role)) {
            return "redirect:/login";
        }

        model.addAttribute("admin", loggedUser);
        model.addAttribute("activePage", "customers");
        model.addAttribute("customers", customerRepository.findAll());
        return "admin/customers";
    }


}