package com.example.nhonApp.config;

import com.example.nhonApp.service.RolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final RolePermissionService rolePermissionService;
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        String username = jwt.getClaim("preferred_username");

        Set<GrantedAuthority> authorities = rolePermissionService.getAuthoritiesForUser(username);

        return new JwtAuthenticationToken(jwt, authorities, username);
    }
}
