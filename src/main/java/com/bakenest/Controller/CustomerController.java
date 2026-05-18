package com.bakenest.Controller;

import com.bakenest.Model.Admin;
import com.bakenest.Model.Customer;
import com.bakenest.Repository.CustomerRepository;
import com.bakenest.Service.AdminService;
import com.bakenest.Service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
                        HttpServletRequest request,
                        Model model) {

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        session = request.getSession(true);

        Optional<Admin> admin = adminService.authenticate(email, password);
        if (admin.isPresent()) {
            session.setAttribute("loggedUser", admin.get());
            session.setAttribute("role", "ADMIN");
            return "redirect:/admin/dashboard";
        }

        Optional<Customer> customerOpt = customerService.authenticate(email, password);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            if (!customer.isActive()) {
                model.addAttribute("loginError", "Your account has been deactivated.");
                // FIX: Add empty object for Thymeleaf binding
                model.addAttribute("customer", new Customer());
                return "login";
            }
            session.setAttribute("loggedUser", customer);
            session.setAttribute("role", "CUSTOMER");
            return "redirect:/customer/product";
        }

        model.addAttribute("loginError", "Invalid email or password");
        // FIX: Add empty object for Thymeleaf binding
        model.addAttribute("customer", new Customer());
        return "login";
    }

    @PostMapping("/admin/customers/toggle/{id}")
    public String toggleCustomerStatus(@PathVariable Long id, RedirectAttributes ra) {
        customerRepository.findById(id).ifPresent(customer -> {
            customer.setActive(!customer.isActive());
            customerRepository.save(customer);
            String status = customer.isActive() ? "activated" : "deactivated";
            ra.addFlashAttribute("success", "Customer " + status + " successfully!");
        });
        return "redirect:/admin/customers";
    }

    @PostMapping("/customer/profile/update")
    public String updateProfile(@Valid @ModelAttribute("user") Customer updatedUser,
                                BindingResult result,
                                HttpSession session,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        Customer currentUser = (Customer) session.getAttribute("loggedUser");

        // 1. Check for standard @Valid errors
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please check your input formats.");
            return "redirect:/customer/profile";
        }

        // 2. Manual Unique Checks - Send to Notification Pill
        if (!updatedUser.getEmail().equalsIgnoreCase(currentUser.getEmail()) &&
                customerRepository.existsByEmail(updatedUser.getEmail())) {
            redirectAttributes.addFlashAttribute("error", "Email is already registered to another account.");
            return "redirect:/customer/profile";
        }

        if (!updatedUser.getNic().equalsIgnoreCase(currentUser.getNic()) &&
                customerRepository.existsByNic(updatedUser.getNic())) {
            redirectAttributes.addFlashAttribute("error", "This NIC number is already in use.");
            return "redirect:/customer/profile";
        }

        if (!updatedUser.getPhoneNumber().equals(currentUser.getPhoneNumber()) &&
                customerRepository.existsByPhoneNumber(updatedUser.getPhoneNumber())) {
            redirectAttributes.addFlashAttribute("error", "This phone number is already registered.");
            return "redirect:/customer/profile";
        }

        // 3. Perform the update if all checks pass
        customerService.updateCustomerProfile(updatedUser, currentUser);
        session.setAttribute("loggedUser", currentUser);

        redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        return "redirect:/customer/profile";
    }

    // --- 1. Update Password Endpoint ---
    @PostMapping("/customer/profile/update-password")
    public String updatePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        Customer currentUser = (Customer) session.getAttribute("loggedUser");

        // 1. Verify new passwords match
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New passwords do not match!");
            return "redirect:/customer/profile";
        }

        // 2. Try to update via service
        boolean isUpdated = customerService.changeCustomerPassword(currentUser, currentPassword, newPassword);

        if (!isUpdated) {
            redirectAttributes.addFlashAttribute("error", "The current password you entered is incorrect.");
            return "redirect:/customer/profile";
        }

        redirectAttributes.addFlashAttribute("success", "Password successfully changed!");
        return "redirect:/customer/profile";
    }

    // --- 2. Deactivate Account Endpoint ---
    @PostMapping("/customer/profile/deactivate")
    public String deactivateAccount(HttpSession session, RedirectAttributes redirectAttributes) {
        Customer currentUser = (Customer) session.getAttribute("loggedUser");

        if (currentUser != null) {
            customerService.deactivateAccount(currentUser);
            // Destroy the session so they are logged out immediately
            session.invalidate();
        }

        // Redirect them to the login page (or homepage) with a parting message
        return "redirect:/login?deactivated=true";
    }
}