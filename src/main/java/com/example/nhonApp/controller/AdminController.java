package com.example.nhonApp.controller;

import com.example.nhonApp.dto.request.AssignRoleRequest;
import com.example.nhonApp.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRoleService userRoleService;

    @PostMapping("/assign-role")
    public ResponseEntity<?> assignRoleInDatabase(@Valid @RequestBody AssignRoleRequest request) throws Exception {
        // Ủy quyền toàn bộ logic cho service
        userRoleService.assignRoleToUser(request.getUsername(), request.getRoleName());

        return ResponseEntity.ok(Map.of("message", "Role '" + request.getRoleName() + "' assigned to user '" + request.getUsername() + "' successfully."));
    }

}
