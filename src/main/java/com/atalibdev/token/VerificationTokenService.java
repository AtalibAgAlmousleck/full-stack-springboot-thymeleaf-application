package com.atalibdev.token;

import com.atalibdev.entitie.User;

import java.util.Optional;

public interface VerificationTokenService {
    String validateToken(String token);
    void saveVerificationTokenForUser(User user, String token);
    Optional<VerificationToken> findByToken(String token);
    void deleteUserToken(Long id);
}
