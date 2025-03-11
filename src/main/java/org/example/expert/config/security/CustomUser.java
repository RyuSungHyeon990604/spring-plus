package org.example.expert.config.security;


import lombok.Getter;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUser implements UserDetails {

    private final Long userId;
    private final String email;
    private final UserRole userRole;

    public CustomUser(Long userId, String email, UserRole userRole) {
        this.userId = userId;
        this.email = email;
        this.userRole = userRole;
    }

    @Override//권한 getter 메서드
    public Collection<? extends GrantedAuthority> getAuthorities() {
        GrantedAuthority authority = new SimpleGrantedAuthority(userRole.name());
        return List.of(authority);
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override//정지 또는 탈퇴 구현할수있음
    public boolean isEnabled() {
        return true;
    }
}
