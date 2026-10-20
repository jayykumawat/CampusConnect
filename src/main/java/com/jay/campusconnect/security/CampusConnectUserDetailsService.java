package com.jay.campusconnect.security;

import com.jay.campusconnect.entity.User;
import com.jay.campusconnect.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CampusConnectUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CampusConnectUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid email or password"));

        return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .roles(user.getRole().name())
                .build();
    }
}
