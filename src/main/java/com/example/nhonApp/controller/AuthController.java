package com.example.nhonApp.controller;

import com.example.nhonApp.dto.request.LoginRequest;
import com.example.nhonApp.dto.request.RefreshTokenRequest;
import com.example.nhonApp.dto.response.TokenKcResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RestTemplate restTemplate;
    @Value("${keycloak.token-uri}")
    private String tokenUri;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    public AuthController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenKcResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        formData.add("grant_type", "password");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("username", loginRequest.getUsername());
        formData.add("password", loginRequest.getPassword());

        TokenKcResponse tokenKcResponse = getToken(formData);
        return ResponseEntity.ok(tokenKcResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenKcResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        formData.add("grant_type", "refresh_token");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("refresh_token", refreshTokenRequest.getRefreshToken());

        TokenKcResponse tokenKcResponse = getToken(formData);
        return ResponseEntity.ok(tokenKcResponse);
    }

    @PostMapping("/client-credentials")
    public ResponseEntity<TokenKcResponse> getServiceToken() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        formData.add("grant_type", "client_credentials");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);

        TokenKcResponse tokenKcResponse = getToken(formData);
        return ResponseEntity.ok(tokenKcResponse);
    }

    private TokenKcResponse getToken(MultiValueMap<String, String> formData) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

        // call api keycloak to get token
        ResponseEntity<TokenKcResponse> responseEntity = restTemplate.postForEntity(
                tokenUri,
                requestEntity,
                TokenKcResponse.class
        );

        return responseEntity.getBody();
    }
}
