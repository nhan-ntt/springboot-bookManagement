package com.example.nhonApp.response;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class TokenKcResponse {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("expires_in")
    private Long expiresIn;
    @JsonProperty("refresh_expires_in")
    private Long refreshExpiresIn;
    private String scope;
}
