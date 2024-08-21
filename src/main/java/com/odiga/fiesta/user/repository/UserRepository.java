package com.odiga.fiesta.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.odiga.fiesta.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

	@Query("SELECT u.userTypeId FROM User u WHERE u.id = :userId")
	Optional<Long> findUserTypeIdById(final Long userId);

	Optional<User> findByEmail(final String email);

	boolean existsByEmail(final String email);
}
