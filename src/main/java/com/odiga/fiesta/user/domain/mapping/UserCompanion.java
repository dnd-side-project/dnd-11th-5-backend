package com.odiga.fiesta.user.domain.mapping;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import com.odiga.fiesta.common.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "user_companion")
@SuperBuilder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class UserCompanion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "user_companion_id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "companion_type_id", nullable = false)
    private Long companionId;

    public static UserCompanion of(Long userId, Long companionId) {
        return UserCompanion.builder()
                .userId(userId)
                .companionId(companionId)
                .build();
    }
}
