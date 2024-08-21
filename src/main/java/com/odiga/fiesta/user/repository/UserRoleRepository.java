package com.odiga.fiesta.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odiga.fiesta.user.domain.mapping.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
}
