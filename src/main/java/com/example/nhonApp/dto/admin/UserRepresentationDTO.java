package com.example.nhonApp.dto.admin;

import lombok.Data;

import java.util.List;

@Data
public class UserRepresentationDTO {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean enabled = true;
    private List<CredentialRepresentationDTO> credentials;
}
