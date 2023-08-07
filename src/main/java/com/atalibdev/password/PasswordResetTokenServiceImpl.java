package com.atalibdev.password;

import com.atalibdev.entitie.User;
import com.atalibdev.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public String validatePasswordResetToken(String token) {
        Optional<PasswordResetToken> passwordResetToken =
                passwordResetTokenRepository.findByToken(token);
        if (passwordResetToken.isEmpty())
            return "invalid";
        Calendar calendar = Calendar.getInstance();
        if ((passwordResetToken.get().getExpirationTime().getTime()-calendar.getTime().getTime()) <= 0)
            return "expired";
        return "valid";
    }

    @Override
    public Optional<User> findUserByPasswordResetToken(String token) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(token)
                .get().getUser());
    }

    @Override
    public void resetPassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String passwordResetToken) {
        PasswordResetToken resetToken = new PasswordResetToken(passwordResetToken, user);
        passwordResetTokenRepository.save(resetToken);
    }
}
