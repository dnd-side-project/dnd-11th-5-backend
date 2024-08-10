package com.odiga.fiesta.user.domain.accounts;

import com.odiga.fiesta.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import static lombok.AccessLevel.PROTECTED;

@Entity
@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@DiscriminatorValue("local")
public class LocalUser extends User {

    @Column(nullable = false)
    private String password;

}
