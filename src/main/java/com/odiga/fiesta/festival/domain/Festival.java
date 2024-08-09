package com.odiga.fiesta.festival.domain;

import com.odiga.fiesta.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Festival extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "festival_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false)
    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private String address;

    @Column(name = "sido_id", nullable = false)
    private Long sidoId;

    @Column(nullable = false)
    private String sigungu;

    @Column(nullable = false)
    private float latitude;

    @Column(nullable = false)
    private float longitude;

    @Column(nullable = false)
    private String tip;

    @Column(name = "homepage_url")
    private String homepageUrl;

    @Column(name = "instagram_url")
    private String instagramUrl;

    private String fee;

    @Column(nullable = false)
    private String description;

    @Column(name = "ticket_link")
    private String ticketLink;

    private String playtime;

    @Column(name = "is_pending", nullable = false)
    private boolean isPending;
}