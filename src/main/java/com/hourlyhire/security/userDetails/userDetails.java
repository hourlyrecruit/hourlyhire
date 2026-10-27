package com.hourlyhire.security.userDetails;

import com.hourlyhire.authentication.entity.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@RequiredArgsConstructor
public class userDetails implements UserDetails {
    private final UserAccount userAccount;
    private final Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return userAccount.getPasswordHash();
    }

    @Override
    public String getUsername(){
        return userAccount.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return userAccount.getAccountStatus() != UserAccount.AccountStatus.LOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return userAccount.getAccountStatus() != UserAccount.AccountStatus.PASSWORD_EXPIRED;
    }

    @Override
    public boolean isEnabled() {
        return userAccount.getAccountStatus() == UserAccount.AccountStatus.ACTIVE;
    }

    public UserAccount getUser() {
        return userAccount;
    }

}
