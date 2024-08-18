package com.odiga.fiesta.user.domain.accounts;

import com.odiga.fiesta.user.domain.User;
import com.odiga.fiesta.user.domain.oauth.OauthProvider;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import static lombok.AccessLevel.PROTECTED;

import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;

@Entity
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = PROTECTED)
@DiscriminatorValue("oauth")
public class OauthUser extends User {

    @Column(name = "provider_id", nullable = false)
    private Long providerId;

    @Enumerated(EnumType.STRING)
    @NotNull
    @ColumnDefault("KAKAO")
    private OauthProvider provider;

    public OauthUser(Long userTypeId, Long roleId, String nickname, String statusMessage, String profileImage,
                     Long providerId, OauthProvider provider) {
        super(userTypeId, roleId, nickname, statusMessage, profileImage); // User 클래스의 필드 초기화
        this.providerId = providerId;
        this.provider = provider;
    }
}
