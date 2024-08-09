package com.odiga.fiesta.user.domain.accounts;

import com.odiga.fiesta.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LocalUser extends BaseEntity {
    @Id
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String password;
}
