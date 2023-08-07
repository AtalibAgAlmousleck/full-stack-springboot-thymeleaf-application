package com.atalibdev.password;

import com.atalibdev.entitie.User;

import java.util.Optional;

public interface PasswordResetTokenService {
    String validatePasswordResetToken(String token);
    Optional<User> findUserByPasswordResetToken(String token);
    void resetPassword(User user, String token);
    void createPasswordResetTokenForUser(User user, String passwordResetToken);
}
