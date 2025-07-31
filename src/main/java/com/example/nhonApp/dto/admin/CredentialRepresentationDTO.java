package com.example.nhonApp.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CredentialRepresentationDTO {
    private String type = "password";
    private String value;
    private boolean temporary = false;
}
