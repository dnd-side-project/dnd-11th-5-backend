package com.odiga.fiesta.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odiga.fiesta.review.domain.ReviewReport;

public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {
}
