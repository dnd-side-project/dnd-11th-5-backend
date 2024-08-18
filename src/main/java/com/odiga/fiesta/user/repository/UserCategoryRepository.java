package com.odiga.fiesta.user.repository;

import com.odiga.fiesta.user.domain.mapping.UserCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCategoryRepository extends JpaRepository<UserCategory, Long> {
}
