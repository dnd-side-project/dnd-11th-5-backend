package com.odiga.fiesta.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odiga.fiesta.user.domain.UserType;

public interface UserTypeRepository extends JpaRepository<UserType, Long> {
    Optional<UserType> findByName(String name);
}
