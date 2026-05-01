package com.bakenest.Controller;

import com.bakenest.Model.Admin;
import com.bakenest.Model.Customer;
import com.bakenest.Repository.CustomerRepository;
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
    private CustomerRepository customerRepository;

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

        // 1. Check Admin first
        Optional<Admin> admin = adminService.authenticate(email, password);
        if (admin.isPresent()) {
            session.setAttribute("loggedUser", admin.get()); // Saves Admin Object
            session.setAttribute("role", "ADMIN");
            return "redirect:/admin/dashboard";
        }

        // 2. FIXED: Fetch the Customer object and save the WHOLE object to session
        Optional<Customer> customer = customerRepository.findByEmail(email);
        if (customer.isPresent() && customer.get().getPassword().equals(password)) {
            session.setAttribute("loggedUser", customer.get()); // Saves Customer Object [FIXED]
            session.setAttribute("role", "CUSTOMER");
            return "redirect:/customer/product";
        }

        model.addAttribute("loginError", "Invalid email or password");
        model.addAttribute("customer", new Customer());
        return "login";
    }
}