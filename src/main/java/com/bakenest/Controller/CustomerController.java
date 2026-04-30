package com.bakenest.Controller;

import com.bakenest.Model.Admin;
import com.bakenest.Model.Customer;
import com.bakenest.Service.AdminService;
import com.bakenest.Service.CustomerService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AdminService adminService;

    // Update in CustomerController.java
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("customer") Customer customer,
                           BindingResult bindingResult,
                           Model model) {

        // Service checks for unique NIC, Email, Phone, and Password match[cite: 7]
        customerService.registerCustomer(customer, bindingResult);

        if (bindingResult.hasErrors()) {
            // Return view name directly to keep error data and stay on the page
            return "login";
        }

        return "redirect:/login?success";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        // 1. First, check if the email belongs to an Admin (Owner)
        Optional<Admin> admin = adminService.authenticate(email, password);
        if (admin.isPresent()) {
            session.setAttribute("loggedUser", admin.get());
            session.setAttribute("role", "ADMIN");
            return "redirect:/admin/dashboard"; // Your admin panel route
        }

        // 2. If no admin match is found, check if it is a Customer
        if (customerService.authenticate(email, password)) {
            session.setAttribute("loggedUser", email);
            session.setAttribute("role", "CUSTOMER");
            return "redirect:/customer/product"; // Standard customer shop
        }

        // 3. If both fail, show the error pill on the login page
        model.addAttribute("loginError", "Invalid email or password");

        // Re-add empty customer to keep the registration tab functional
        model.addAttribute("customer", new Customer());
        return "login";
    }
}