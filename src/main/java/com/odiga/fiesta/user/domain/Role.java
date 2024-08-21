package com.odiga.fiesta.user.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.odiga.fiesta.auth.domain.Authority;

@Entity
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "`role`")
@Builder
public class Role {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "role_id")
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "authority", nullable = false, unique = true)
	private Authority authority;

	public static Role of(Authority authority) {
		return Role.builder()
			.authority(authority)
			.build();
	}

	public static SimpleGrantedAuthority toGrantedAuthority(Role role) {
		return new SimpleGrantedAuthority(role.getAuthority().name());
	}
}
