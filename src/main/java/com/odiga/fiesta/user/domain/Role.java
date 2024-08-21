package com.odiga.fiesta.user.domain;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.odiga.fiesta.auth.domain.Authority;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
