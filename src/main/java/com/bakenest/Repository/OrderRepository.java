package com.bakenest.Repository;

import com.bakenest.Model.Order;
import com.bakenest.Model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerOrderByOrderDateDesc(Customer customer);

    List<Order> findAllByOrderByOrderDateDesc();

    List<Order> findByStatusOrderByOrderDateDesc(String status);
}
