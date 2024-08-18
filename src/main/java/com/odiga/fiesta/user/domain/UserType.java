package com.odiga.fiesta.user.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "`user_type`")
@Builder
public class UserType {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "user_type_id")
    private Long id;

    private String name;

    @Column(name = "profile_image", length = 1024)
    private String profileImage;
}