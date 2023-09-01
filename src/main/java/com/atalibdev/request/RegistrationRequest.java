package com.atalibdev.request;

import com.atalibdev.entitie.Role;
import lombok.Data;

import java.util.List;

@Data
public class RegistrationRequest {
    private String username;
    //private String lastName;
    private String email;
    private String password;
    private List<Role> roles;
}
