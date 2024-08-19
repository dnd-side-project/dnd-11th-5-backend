package com.odiga.fiesta.user.domain.accounts;

import static lombok.AccessLevel.*;

import com.odiga.fiesta.user.domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = PROTECTED)
@DiscriminatorValue("local")
public class LocalUser extends User {

    @Column(nullable = false)
    private String password;

}
