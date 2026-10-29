package com.hourlyhire.security.userDetails;

import com.hourlyhire.authentication.entity.UserAccount;
import com.hourlyhire.authentication.enums.Role;
import com.hourlyhire.authentication.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        UserAccount userAccount = userAccountRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Invalid username or email."
                        ));

        Collection<GrantedAuthority> authorities = getAuthorities(userAccount);

        return new CustomUserDetails(userAccount, authorities);

    }

    private Collection<GrantedAuthority> getAuthorities(UserAccount user) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        Role role = user.getRole();
        if (role != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
            switch (role) {

                default:
                    break;
            }
        }
        return authorities;
    }

}