package com.odiga.fiesta.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.odiga.fiesta.user.domain.accounts.OauthUser;

public interface OauthUserRepository extends JpaRepository<OauthUser, Long> {

    boolean existsByProviderId(Long providerId);

    Optional<OauthUser> findByProviderId(Long providerId);
}
