package com.odiga.fiesta.review.domain;

import com.odiga.fiesta.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@AllArgsConstructor
@Getter
@SuperBuilder
@NoArgsConstructor(access = PROTECTED)
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "festival_id", nullable = false)
    private Long festivalId;

    @Column(nullable = false)
    private int score;

    @Column(nullable = false)
    private String content;
}
