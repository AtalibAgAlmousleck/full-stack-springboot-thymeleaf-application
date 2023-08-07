package com.atalibdev.controller;

import com.atalibdev.email.UserRegistrationCompleteEventListener;
import com.atalibdev.entitie.User;
import com.atalibdev.email.UserRegistrationCompleteEvent;
import com.atalibdev.password.PasswordResetTokenService;
import com.atalibdev.request.RegistrationRequest;
import com.atalibdev.service.UserService;
import com.atalibdev.token.VerificationToken;
import com.atalibdev.token.VerificationTokenServiceImpl;
import com.atalibdev.utility.UrlUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;
    private final ApplicationEventPublisher publisher;
    private final VerificationTokenServiceImpl verificationTokenService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final UserRegistrationCompleteEventListener userRegistrationCompleteEventListener;

    @GetMapping("/registration-form")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new RegistrationRequest());
        return "registration";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") RegistrationRequest registration,
                           HttpServletRequest request) {
        User user = userService.register(registration);
        publisher.publishEvent(new UserRegistrationCompleteEvent(user, UrlUtil.getApplicationUrl(request)));
        return "redirect:/registration/registration-form?success";
    }

    @GetMapping("/verifyEmail") //verifyEmail
    public String emailVerification(@RequestParam("token") String token) {
        Optional<VerificationToken> verificationToken =
                verificationTokenService.findByToken(token);
        if (verificationToken.isPresent() && verificationToken.get().getUser().isEnabled()){
            return "redirect:/login?verified";
        }
        String verificationResult = verificationTokenService.validateToken(token);
        return switch (verificationResult.toLowerCase()) {
            case "expired" -> "redirect:/error?expired";
            case "valid" -> "redirect:/login?valid";
            default -> "redirect:/error?invalid";
        };
    }

    @GetMapping("/forgot-password-request")
    public String forgotPasswordForm() {
        return "forgot-password-form";
    }

    @PostMapping("/forgot-password")
    public String resetPasswordRequest(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        Optional<User> user = userService.findByEmail(email);
        if (user.isEmpty()){
            return "redirect:/registration/forgot-password-request?not_fond";
        }
        String passwordResetToken = UUID.randomUUID().toString();
        passwordResetTokenService.createPasswordResetTokenForUser(user.get(), passwordResetToken);
        // todo: send password reset email to the user
        String url = UrlUtil.getApplicationUrl(request)+"/registration/password-reset-form?token="+passwordResetToken;
        try {
            userRegistrationCompleteEventListener
                    .sendPasswordResetVerificationEmail(url);
        }catch (MessagingException | UnsupportedEncodingException ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "redirect:/registration/forgot-password-request?success";
    }

    @GetMapping("/password-reset-form")
    public String passwordResetForm(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "password-reset-form";
    }

    @PostMapping("/reset-password")
    public String resetPassword(HttpServletRequest  request) {
        String theToken = request.getParameter("token");
        String password = request.getParameter("password");
        String tokenVerificationResult = passwordResetTokenService.validatePasswordResetToken(theToken);
        if (!tokenVerificationResult.equalsIgnoreCase("valid")){
            return "redirect:/error?invalid_token";
        }
        Optional<User> theUser = passwordResetTokenService.findUserByPasswordResetToken(theToken);
        if (theUser.isPresent()){
            passwordResetTokenService.resetPassword(theUser.get(), password);
            return "redirect:/login?reset_success";
        }
        return "redirect:/error?not_found";
    }
}
