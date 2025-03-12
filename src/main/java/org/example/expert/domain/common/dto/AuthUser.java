package org.example.expert.domain.common.dto;

import lombok.Getter;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Getter
public class AuthUser {

    private final Long id;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;

    public AuthUser(Long id, String email, UserRole role) {
        this.id = id;
        this.email = email;
        this.authorities = List.of(new SimpleGrantedAuthority(role.name()));
    }

    public UserRole getUserRole() {
        if(this.authorities != null && !this.authorities.isEmpty()) {
            return UserRole.of(authorities.iterator().next().getAuthority());
        }
        throw new InvalidRequestException("유효하지 않은 UerRole");
    }
}
