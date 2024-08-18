package com.odiga.fiesta.category.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odiga.fiesta.category.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	List<Category> findByIdIn(List<Long> categoryIds);
}
