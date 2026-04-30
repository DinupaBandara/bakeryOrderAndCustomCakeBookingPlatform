package com.bakenest.Service;

import com.bakenest.Model.Admin;
import com.bakenest.Repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    public Optional<Admin> authenticate(String email, String password) {
        // Checks if the email exists in the admin table and verifies the password
        return adminRepository.findByEmail(email)
                .filter(admin -> admin.getPassword().equals(password));
    }
}