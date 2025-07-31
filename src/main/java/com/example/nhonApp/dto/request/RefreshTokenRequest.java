package com.example.nhonApp.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {
    @NotBlank(message = "Refresh token không được để trống")
    @JsonProperty("refresh_token") // Giống tên field trong request body
    private String refreshToken;
}