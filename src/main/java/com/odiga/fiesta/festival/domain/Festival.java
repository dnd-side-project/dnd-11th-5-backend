package com.odiga.fiesta.festival.domain;

import com.odiga.fiesta.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
public class Festival extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
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
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private String tip;

    @Column(name = "homepage_url", length = 1024)
    private String homepageUrl;

    @Column(name = "instagram_url", length = 1024)
    private String instagramUrl;

    private String fee;

    @Column(nullable = false)
    private String description;

    @Column(name = "ticket_link", length = 1024)
    private String ticketLink;

    private String playtime;

    @Column(name = "is_pending", nullable = false)
    private boolean isPending;

}