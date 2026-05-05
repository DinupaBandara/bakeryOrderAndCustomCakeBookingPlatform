package com.bakenest.Repository;

import com.bakenest.Model.Feedback;
import com.bakenest.Model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    // Check if feedback already exists for an order
    boolean existsByOrder(Order order);
    List<Feedback> findAllByOrderByCreatedAtDesc();
}