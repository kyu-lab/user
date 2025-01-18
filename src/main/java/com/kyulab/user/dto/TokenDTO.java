package com.kyulab.user.dto;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public record TokenDTO(String id, String userName, Collection<? extends GrantedAuthority> roles) {

}
