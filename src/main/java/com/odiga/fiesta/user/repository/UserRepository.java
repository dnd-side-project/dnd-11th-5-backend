package com.odiga.fiesta.user.repository;

import java.util.Optional;

import javax.swing.text.html.Option;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.odiga.fiesta.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

	@Query("SELECT u.userTypeId FROM User u WHERE u.id = :userId")
	Optional<Long> findUserTypeIdById(Long userId);
}
