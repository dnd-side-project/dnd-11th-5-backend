package com.odiga.fiesta.user.repository;

import com.odiga.fiesta.user.domain.UserType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTypeRepository extends JpaRepository<UserType, Long> {
    Optional<UserType> findByName(String name);
}
