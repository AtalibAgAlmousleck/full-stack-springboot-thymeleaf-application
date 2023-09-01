package com.atalibdev.email;

import com.atalibdev.entitie.User;
import com.atalibdev.token.VerificationTokenServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class UserRegistrationCompleteEventListener implements ApplicationListener<UserRegistrationCompleteEvent> {

    private final JavaMailSender mailSender;
    private final VerificationTokenServiceImpl verificationTokenService;
    private User user;

    @Override
    public void onApplicationEvent(UserRegistrationCompleteEvent event) {
        //todo 1. get the user
        user = event.getUser();
        //todo 2. generate a token for the user
        String verifyToken = UUID.randomUUID().toString();
        //todo 3. save the token for the user
        verificationTokenService.saveVerificationTokenForUser(user, verifyToken);
        //todo 4. Build the verification url
        String url = event.getConfirmationUrl()+"/registration/verifyEmail?token="+verifyToken;
        //todo 5. send the email to the user
        try {
            sendVerificationEmail(url);
        } catch (MessagingException | UnsupportedEncodingException ex) {
            throw new RuntimeException();
        }
    }

    public void sendVerificationEmail(String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Email Verification";
        String senderName = "Users Verification Service";
        String mailContent = "<p> Hi, "+ user.getUsername()+ ", </p>"+
                "<p>Thank you for registering with us,"+"" +
                "Please, follow the link below to complete your registration.</p>"+
                "<a href=\"" +url+ "\">Verify your email to activate your account</a>"+
                "<p> Thank you <br> Users Registration Portal Service";
        emailMessage(subject, senderName, mailContent, mailSender, user);
    }

    public void sendPasswordResetVerificationEmail(String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Password Reset Request Verification";
        String senderName = "Users Verification Service";
        String mailContent = "<p> Hi, "+ user.getUsername()+ ", </p>"+
                "<p><b>You recently requested to reset your password,</b>"+"" +
                "Please, follow the link below to complete the action.</p>"+
                "<a href=\"" +url+ "\">Reset password</a>"+
                "<p> Users Registration Portal Service </p>";
        emailMessage(subject, senderName, mailContent, mailSender, user);
    }

    public static void emailMessage(String subject, String senderName,
                                    String mailContent, JavaMailSender mailSender,
                                    User user) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("atalibcodinglevel@gmail.com", senderName);
        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        mailSender.send(message);
    }
}
