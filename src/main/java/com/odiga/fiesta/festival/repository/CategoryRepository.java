package com.odiga.fiesta.festival.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odiga.fiesta.festival.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
