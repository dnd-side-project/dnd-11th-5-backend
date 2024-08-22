package com.odiga.fiesta.auth.domain;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;

@Getter
public class UserAccount extends User {

	public com.odiga.fiesta.user.domain.User account;

	public UserAccount(com.odiga.fiesta.user.domain.User account) {
		super(account.getEmail(), "", List.of(new SimpleGrantedAuthority("ROLE_USER")));
		this.account = account;
	}
}
