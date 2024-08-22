package com.odiga.fiesta.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odiga.fiesta.auth.domain.Authority;
import com.odiga.fiesta.user.domain.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

	Optional<Role> findByAuthority(Authority authority);
}
