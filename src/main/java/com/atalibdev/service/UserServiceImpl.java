package com.atalibdev.service;

import com.atalibdev.entitie.Role;
import com.atalibdev.entitie.User;
import com.atalibdev.repository.UserRepository;
import com.atalibdev.request.RegistrationRequest;
import com.atalibdev.token.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService verificationTokenService;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User register(RegistrationRequest request) {
        var user = new User(request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                List.of(new Role("ROLE_USER")));
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.empty();
    }

    @Transactional
    @Override
    public void updateUser(Long id, String username, String email) {
        userRepository.update(username, email, id);
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        user.ifPresent(theUser -> verificationTokenService
                .deleteUserToken(theUser.getId()));
        userRepository.deleteById(id);
    }
}
