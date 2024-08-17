package com.odiga.fiesta.user.repository;

import com.odiga.fiesta.user.domain.accounts.OauthUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OauthUserRepository extends JpaRepository<OauthUser, Long> {

    boolean existsByProviderId(Long providerId);

    Optional<OauthUser> findByProviderId(Long providerId);
}
