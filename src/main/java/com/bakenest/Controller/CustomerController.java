package com.bakenest.Controller;

import com.bakenest.Model.Customer;
import com.bakenest.Service.CustomerService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    // 2. Handle Registration
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("customer") Customer customer, RedirectAttributes redirectAttributes) {
        try {
            customerService.registerCustomer(customer);
            redirectAttributes.addFlashAttribute("successMessage", "Account created! Please sign in.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Registration failed. Email might already exist.");
        }
        // FIX: Change to /login
        return "redirect:/login";
    }

    // 3. Handle Login
    @PostMapping("/login")
    public String loginUser(@RequestParam("email") String email,
                            @RequestParam("password") String password,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {

        Customer customer = customerService.authenticate(email, password);

        if (customer != null) {
            session.setAttribute("loggedInUser", customer);
            return "redirect:/product"; // Redirect to home page
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid email or password.");
            // FIX: Change to /login
            return "redirect:/login";
        }
    }

    // 4. Handle Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        // FIX: Change to /login
        return "redirect:/login";
    }
}