package com.odiga.fiesta.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odiga.fiesta.review.domain.Review;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewCustomRepository {

	List<Review> findAllByFestivalId(Long festivalId);

	Boolean existsByUserId(Long userId);

	Long countByUserId(Long userId);
}
