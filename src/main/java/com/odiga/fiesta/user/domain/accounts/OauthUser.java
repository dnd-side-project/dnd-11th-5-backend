package com.odiga.fiesta.user.domain.accounts;

import com.odiga.fiesta.user.domain.User;
import com.odiga.fiesta.user.oauth.OauthProvider;
import jakarta.persistence.*;
import lombok.*;
import static lombok.AccessLevel.PROTECTED;

@Entity
@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@DiscriminatorValue("oauth")
public class OauthUser extends User {

    @Column(name = "provider_id", nullable = false)
    private Long providerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'KAKAO'")
    private OauthProvider provider;

}
