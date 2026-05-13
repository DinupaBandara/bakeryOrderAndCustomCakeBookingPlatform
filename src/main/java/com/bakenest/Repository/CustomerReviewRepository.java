package com.bakenest.Repository;

import com.bakenest.Model.CustomerReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerReviewRepository extends JpaRepository<CustomerReview, Long> {

    // Loads the newest reviews first, which is the order users expect on a review page.
    List<CustomerReview> findAllByOrderByCreatedAtDesc();
}
