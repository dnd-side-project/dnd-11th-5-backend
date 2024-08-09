package com.odiga.fiesta.user.domain.repository;

import com.odiga.fiesta.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
