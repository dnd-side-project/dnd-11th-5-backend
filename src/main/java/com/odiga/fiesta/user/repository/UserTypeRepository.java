package com.odiga.fiesta.user.repository;

import com.odiga.fiesta.user.domain.UserType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTypeRepository extends JpaRepository<UserType, Long> {
    UserType findByName(String name);
}
