package com.odiga.fiesta.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.odiga.fiesta.auth.domain.UserAccount;
import com.odiga.fiesta.user.domain.User;
import com.odiga.fiesta.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User account = userRepository.findByEmail(email)
			.orElseThrow(() -> new UsernameNotFoundException(email));
		
		return new UserAccount(account);
	}
}
