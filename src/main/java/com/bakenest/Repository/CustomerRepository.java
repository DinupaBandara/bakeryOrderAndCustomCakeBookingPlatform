package com.bakenest.Repository;

import com.bakenest.Model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Spring Data JPA magically writes the SQL query for this based on the method name!
    Customer findByEmail(String email);
}