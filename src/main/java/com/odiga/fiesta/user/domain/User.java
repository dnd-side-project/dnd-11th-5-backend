package com.odiga.fiesta.user.domain;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import com.odiga.fiesta.common.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "account_type")
@Table(name = "`user`")
@SuperBuilder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_type_id")
    private Long userTypeId;

    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Column(nullable = false)
    private String nickname;

    @Column(name = "status_message")
    private String statusMessage;

    @Column(name = "profile_image", length = 1024)
    private String profileImage;

}
