package com.odiga.fiesta.global.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odiga.fiesta.global.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
