package com.bakenest.Repository;

import com.bakenest.Model.Bill;
import com.bakenest.Model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {

    Optional<Bill> findByOrder(Order order);
}
