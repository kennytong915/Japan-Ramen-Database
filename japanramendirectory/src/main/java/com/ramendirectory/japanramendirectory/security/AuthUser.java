package com.ramendirectory.japanramendirectory.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.ramendirectory.japanramendirectory.model.User;



public class AuthUser implements org.springframework.security.core.userdetails.UserDetails{
	private User user;

	public AuthUser(User user) {
		super();
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		System.out.println(new SimpleGrantedAuthority(this.user.getRole().toString()));
		return Arrays.asList(new SimpleGrantedAuthority(this.user.getRole().toString()));
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return this.user.getPassword();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return this.user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// Check if account is locked
		if (user.getAccountNonLocked() != null && !user.getAccountNonLocked()) {
			// If there's a lock expiry time, check if it's passed
			if (user.getLockedUntil() != null) {
				return new Date().after(user.getLockedUntil());
			}
			return false;
		}
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}
	
}
