package com.example.nhonApp.service;

import com.example.nhonApp.entity.Permission;
import com.example.nhonApp.entity.RolePermission;
import com.example.nhonApp.repository.PermissionRepository;
import com.example.nhonApp.repository.RolePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionCacheService {

    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;

    // cache list cac ten permission cua role

    @Cacheable(value = "rolePermissionNames", key = "#roleId")
    @Transactional(readOnly = true)
    public Set<String> getPermissionNamesForRole(Long roleId) {
        System.out.println("--- DATABASE QUERY: Lấy quyền cho roleId: " + roleId);

        List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleIdIn(List.of(roleId));
        if (rolePermissions.isEmpty()) {
            return Set.of();
        }

        List<Long> permissionIds = rolePermissions.stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toList());

        List<Permission> permissions = permissionRepository.findAllById(permissionIds);

        return permissions.stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());

    }
}
