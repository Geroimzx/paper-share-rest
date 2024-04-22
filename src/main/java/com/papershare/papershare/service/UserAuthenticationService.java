package com.papershare.papershare.service;

import com.papershare.papershare.model.User;

import java.util.List;
import java.util.Optional;


public interface UserAuthenticationService {

    void createUser(User user);

    Optional<User> findByUsername(String username);

    List<User> findAllUsers();

    User updateUser(User user);

    void deleteUser(String username);
}