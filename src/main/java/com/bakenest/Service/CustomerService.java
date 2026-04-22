package com.bakenest.Service;

import com.bakenest.Model.Customer;
import com.bakenest.Repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // Lombok generates the constructor for the repository
public class CustomerService {

    private final CustomerRepository customerRepository;

    // Register a new customer
    public Customer registerCustomer(Customer customer) {
        // NOTE: In a real production app, you MUST encrypt the password here using
        // BCryptPasswordEncoder before saving it to the database!
        return customerRepository.save(customer);
    }

    // Authenticate user for login
    public Customer authenticate(String email, String password) {
        Customer customer = customerRepository.findByEmail(email);

        // Check if customer exists AND password matches
        if (customer != null && customer.getPassword().equals(password)) {
            return customer;
        }
        return null; // Login failed
    }
}