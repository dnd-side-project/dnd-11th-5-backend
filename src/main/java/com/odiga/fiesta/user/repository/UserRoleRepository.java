package com.odiga.fiesta.user.repository;

import com.odiga.fiesta.user.domain.mapping.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
}
