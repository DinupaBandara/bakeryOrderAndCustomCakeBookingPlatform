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

    public void registerAdmin(Admin admin) throws Exception {
        if (adminRepository.findByEmail(admin.getEmail()).isPresent()) {
            throw new Exception("An account with this email already exists!");
        }
        adminRepository.save(admin);
    }

    public void updateAdmin(Admin admin) throws Exception {
        Admin existing = adminRepository.findById(admin.getId())
                .orElseThrow(() -> new Exception("Admin not found"));

        existing.setName(admin.getName());
        existing.setEmail(admin.getEmail());

        // Lombok creates setSuperAdmin for a field named superAdmin
        existing.setSuperAdmin(admin.isSuperAdmin());

        if (admin.getPassword() != null && !admin.getPassword().isEmpty()) {
            existing.setPassword(admin.getPassword());
        }
        adminRepository.save(existing);
    }

    public void deleteAdmin(Long id) throws Exception {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new Exception("Admin not found"));
        if (admin.isSuperAdmin()) {
            throw new Exception("Super Admins cannot be deleted!");
        }
        adminRepository.deleteById(id);
    }
}