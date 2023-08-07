package com.atalibdev.email;

import com.atalibdev.entitie.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class UserRegistrationCompleteEvent extends ApplicationEvent {

    private User user;
    private String confirmationUrl;

    public UserRegistrationCompleteEvent(User user, String confirmationUrl) {
        super(user);
        this.user = user;
        this.confirmationUrl = confirmationUrl;

    }
}
