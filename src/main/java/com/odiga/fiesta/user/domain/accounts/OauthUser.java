package com.odiga.fiesta.user.domain.accounts;

import com.odiga.fiesta.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OauthUser extends BaseEntity {
    @Id
    @Column(name = "user_id")
    private Long id;

    @Column(name = "provider_id", nullable = false)
    private Long providerId;

    @Column(nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'Kakao'")
    private String provider;
}
