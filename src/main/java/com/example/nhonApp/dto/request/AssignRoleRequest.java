package com.example.nhonApp.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AssignRoleRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String roleName;
}