package com.timely.userservice.model;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.firebase.auth.UserRecord;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

    private UserRecord userRecord;

    public static final String ASSOCIATE_ROLE = "ROLE_ASSOCIATE";
    public static final String MANAGER_ROLE = "ROLE_MANAGER";

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<String> claims = userRecord.getCustomClaims().keySet();
        return claims.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    public String getEmail() {
        return userRecord.getEmail();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return userRecord.getDisplayName();
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

    @Override
    public boolean isEnabled() {
        return !userRecord.isDisabled();
    }

}
