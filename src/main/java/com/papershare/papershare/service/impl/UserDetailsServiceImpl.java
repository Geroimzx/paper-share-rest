package com.papershare.papershare.service.impl;


import com.papershare.papershare.model.User;
import com.papershare.papershare.model.UserDetailsImpl;
import com.papershare.papershare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserRepository userRepository;


    public UserDetailsServiceImpl() {
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.map(UserDetailsImpl::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
}