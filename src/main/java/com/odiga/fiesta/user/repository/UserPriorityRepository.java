package com.odiga.fiesta.user.repository;

import com.odiga.fiesta.user.domain.mapping.UserPriority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPriorityRepository extends JpaRepository<UserPriority, Long> {
}
