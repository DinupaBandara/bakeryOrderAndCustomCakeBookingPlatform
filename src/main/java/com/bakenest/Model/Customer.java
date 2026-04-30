package com.bakenest.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Existing fields...
    @Column(name = "first_name", nullable = false, length = 25)
    @NotBlank(message = "First name is required")
    @Size(max = 25)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 25)
    @NotBlank(message = "Last name is required")
    @Size(max = 25)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 75)
    @Email(message = "Email should be valid")
    @NotBlank
    private String email;

    // --- New Fields ---

    @Column(name = "nic", nullable = false, unique = true, length = 15)
    @NotBlank(message = "NIC is required")
    @Size(min = 9, max = 15, message = "NIC must be between 9 and 15 characters")
    private String nic;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "city", length = 20)
    private String city;

    @Column(name = "zip_code", length = 10)
    @Pattern(regexp = "^[0-9]{5}(?:-[0-9]{4})?$", message = "Invalid Zip Code format")
    private String zipCode;

    // Existing fields...
    @Column(name = "phone_number",nullable = false, length = 10)
    @NotBlank(message = "Phone Number code is required")
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be 10 digits")
    private String phoneNumber;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "password", nullable = false)
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @Transient
    private String confirmPassword;
}