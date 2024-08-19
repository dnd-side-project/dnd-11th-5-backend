package com.odiga.fiesta.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odiga.fiesta.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
