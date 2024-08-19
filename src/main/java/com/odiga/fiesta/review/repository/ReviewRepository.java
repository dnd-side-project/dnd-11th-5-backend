package com.odiga.fiesta.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odiga.fiesta.review.domain.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
