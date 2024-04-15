package com.papershare.papershare.service.impl;

import com.papershare.papershare.model.Role;
import com.papershare.papershare.model.RoleName;
import com.papershare.papershare.model.User;
import com.papershare.papershare.repository.UserRepository;
import com.papershare.papershare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public User createUser(User user) {
        Role role = new Role();
        role.setName(RoleName.ROLE_USER);
        user.setRoles(Collections.singletonList(role));
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User updateUser(User user) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            existingUser.get().setFirstName(user.getFirstName());
            existingUser.get().setLastName(user.getLastName());
            existingUser.get().setEmail(user.getEmail());
            return userRepository.save(existingUser.get());
        } else {
            return null;
        }
    }

    @Transactional
    public void deleteUser(String username) {
        userRepository.deleteByUsername(username);
    }

    @Override
    public boolean login(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return user.getPassword().equals(password);
        }
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<GrantedAuthority> authorities = user.getRoles().stream()
                    .map(Role::getName)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}