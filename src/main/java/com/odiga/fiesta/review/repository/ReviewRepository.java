package com.odiga.fiesta.review.repository;

import com.odiga.fiesta.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
