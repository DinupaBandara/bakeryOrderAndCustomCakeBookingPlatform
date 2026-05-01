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
        return "/customer/bakeryItem";
    }

    @GetMapping("/admin/dashboard")
    public String showAdminDashboardPage(HttpSession session, Model model) {
        Object loggedUser = session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/login";
        }

        // Adding it to the model allows you to use ${admin} if you prefer
        model.addAttribute("admin", loggedUser);
        model.addAttribute("activePage", "dashboard");
        return "admin/dashboard";
    }

    @GetMapping("/admin/product")
    public String showAdminProductPage(Model model, HttpSession session) {
        Object loggedUser = session.getAttribute("loggedUser");

        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/login";
        }

        List<Product> productList = productRepository.findAll();
        model.addAttribute("products", productList);
        model.addAttribute("admin", loggedUser);
        model.addAttribute("activePage", "products");

        return "admin/product";
    }

    @GetMapping("/admin/adminManagement")
    public String showManagementPage(Model model, HttpSession session) {

        // 1. Retrieve the session user
        Admin loggedIn = (Admin) session.getAttribute("loggedUser");

        // 2. Security Check: Block unauthorized access
        if (loggedIn == null || !loggedIn.isSuperAdmin()) {
            return "redirect:/admin/dashboard";
        }

        // 3. Provide data to the view
        model.addAttribute("admin", loggedIn);
        model.addAttribute("admins", adminRepository.findAll());
        model.addAttribute("activePage", "management");

        return "Admin/adminManagement";
    }

    @GetMapping("/admin/profile")
    public String showProfilePage(Model model, HttpSession session) {
        // 1. Retrieve the session user
        Admin loggedIn = (Admin) session.getAttribute("loggedUser");

        // 2. Security Check
        if (loggedIn == null) {
            return "redirect:/login";
        }

        // 3. Pass the logged-in admin to the view
        model.addAttribute("admin", loggedIn);
        model.addAttribute("activePage", "profile");

        return "Admin/profile";
    }

    @GetMapping("/admin/customers")
    public String showCustomerPage(Model model, HttpSession session) {
        // 1. Retrieve the session user
        Admin loggedIn = (Admin) session.getAttribute("loggedUser");

        // 2. Security Check
        if (loggedIn == null) {
            return "redirect:/login";
        }

        // 3. Pass the logged-in admin to the view
        model.addAttribute("admin", loggedIn);
        model.addAttribute("activePage", "customers");
        model.addAttribute("customers", customerRepository.findAll());

        return "admin/customers";
    }


}