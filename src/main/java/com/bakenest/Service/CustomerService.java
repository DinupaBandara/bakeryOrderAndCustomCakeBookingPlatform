package com.bakenest.Service;

import com.bakenest.Model.Customer;
import com.bakenest.Repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public void registerCustomer(Customer customer, BindingResult result) {
        // 1. Check for duplicate Email
        if (customerRepository.existsByEmail(customer.getEmail())) {
            result.rejectValue("email", "error.customer", "Email address is already registered.");
        }

        // 2. Check for duplicate NIC
        if (customerRepository.existsByNic(customer.getNic())) {
            result.rejectValue("nic", "error.customer", "National ID is already registered.");
        }

        if (customerRepository.existsByPhoneNumber(customer.getPhoneNumber())) {
            result.rejectValue("phoneNumber", "error.customer", "Phone number is already registered.");
        }

        // 3. Validate password confirmation
        if (customer.getPassword() != null && !customer.getPassword().equals(customer.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.customer", "Passwords do not match.");
        }

        // 4. Save only if there are no validation or business logic errors-[cite: 1, 2]
        if (!result.hasErrors()) {
            // In a production app, hash the password here (e.g., using BCrypt)[cite: 2]
            customerRepository.save(customer);
        }
    }

    public Optional<Customer> authenticate(String email, String password) {
        return customerRepository.findByEmail(email)
                .filter(c -> c.getPassword().equals(password));
    }

    public void updateCustomerProfile(Customer updatedData, Customer currentUser) {
        // 1. Update personal details
        currentUser.setFirstName(updatedData.getFirstName());
        currentUser.setLastName(updatedData.getLastName());
        currentUser.setEmail(updatedData.getEmail());
        currentUser.setPhoneNumber(updatedData.getPhoneNumber());

        // 2. Update NIC and location details
        currentUser.setNic(updatedData.getNic());
        currentUser.setAddress(updatedData.getAddress());
        currentUser.setCity(updatedData.getCity());
        currentUser.setZipCode(updatedData.getZipCode());

        // 3. Persist changes
        customerRepository.save(currentUser);
    }
}