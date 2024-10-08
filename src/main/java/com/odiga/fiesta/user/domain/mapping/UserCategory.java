package com.odiga.fiesta.user.domain.mapping;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import com.odiga.fiesta.common.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "user_category", indexes = {
    @Index(name = "idx_user_category", columnList = "user_id, category_id")
})
@SuperBuilder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class UserCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "user_category_id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    public static UserCategory of(Long userId, Long categoryId) {
        return UserCategory.builder()
                .userId(userId)
                .categoryId(categoryId)
                .build();
    }
}
