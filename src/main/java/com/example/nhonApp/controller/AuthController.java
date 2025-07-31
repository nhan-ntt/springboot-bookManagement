package com.example.nhonApp.controller;

import com.example.nhonApp.dto.admin.CredentialRepresentationDTO;
import com.example.nhonApp.dto.admin.UserRepresentationDTO;
import com.example.nhonApp.dto.request.LoginRequest;
import com.example.nhonApp.dto.request.RefreshTokenRequest;
import com.example.nhonApp.dto.request.RegisterRequest;
import com.example.nhonApp.dto.response.TokenKcResponse;
import com.example.nhonApp.entity.User;
import com.example.nhonApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;

    @Value("${keycloak.token-uri}")
    private String tokenUri;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.admin.url}")
    private String keycloakAdminUrl;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            String adminToken = getAdminToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(adminToken);

            UserRepresentationDTO user = getUserRepresentationDTO(registerRequest);

            HttpEntity<UserRepresentationDTO> requestEntity = new HttpEntity<>(user, headers);

            restTemplate.postForEntity(keycloakAdminUrl + "/users", requestEntity, String.class);

            User localUser = new User();
            localUser.setUsername(registerRequest.getUsername());
            localUser.setEmail(registerRequest.getEmail());

            userRepository.save(localUser);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "User registered successfully"));

        } catch (HttpClientErrorException e) {
        // Bắt lỗi cụ thể, ví dụ user đã tồn tại (409 Conflict)
        if (e.getStatusCode() == HttpStatus.CONFLICT) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "User or email already exists"));
        }
        return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", e.getResponseBodyAsString()));
    }
    }

    private static UserRepresentationDTO getUserRepresentationDTO(RegisterRequest registerRequest) {
        CredentialRepresentationDTO credential = new CredentialRepresentationDTO(
                "password", registerRequest.getPassword(), false
        );

        UserRepresentationDTO user = new UserRepresentationDTO();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setCredentials(Collections.singletonList(credential));
        return user;
    }

    private String getAdminToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "client_credentials");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

        try {
            ResponseEntity<TokenKcResponse> response = restTemplate.postForEntity(
                    tokenUri,
                    requestEntity,
                    TokenKcResponse.class
            );
            return Objects.requireNonNull(response.getBody()).getAccessToken();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get admin token: " + e.getMessage(), e);
        }
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
