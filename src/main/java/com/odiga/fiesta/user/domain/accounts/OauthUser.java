package com.odiga.fiesta.user.domain.accounts;

import static lombok.AccessLevel.*;

import org.hibernate.annotations.ColumnDefault;

import com.odiga.fiesta.user.domain.User;
import com.odiga.fiesta.user.domain.oauth.OauthProvider;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = PROTECTED)
@DiscriminatorValue("oauth")
@SuperBuilder
public class OauthUser extends User {

    @Column(name = "provider_id", nullable = false)
    private Long providerId;

    @Enumerated(EnumType.STRING)
    @NotNull
    @ColumnDefault("KAKAO")
    private OauthProvider provider;
}
