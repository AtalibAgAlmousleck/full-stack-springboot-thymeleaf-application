package com.atalibdev.service;

import com.atalibdev.entitie.User;
import com.atalibdev.request.RegistrationRequest;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    User register(RegistrationRequest request);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    void updateUser(Long id, String username, String email);
    void deleteUser(Long id);

}
