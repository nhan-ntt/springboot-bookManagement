package com.example.nhonApp.service;

import com.example.nhonApp.entity.*;
import com.example.nhonApp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolePermissionService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public Set<GrantedAuthority> getAuthoritiesForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Set<GrantedAuthority> authorities = new HashSet<>();

        List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
        List<Role> roles = roleRepository.findAllById(
                userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList())
        );

        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));

            // !!! CẢNH BÁO: Dòng này gây ra lỗi "self-invocation" !!!
            // Cache sẽ không hoạt động vì đây là cuộc gọi bên trong cùng một object,
            // nó bỏ qua proxy của Spring.
            Collection<GrantedAuthority> permissions = this.getPermissionsForRole(role.getId());

            authorities.addAll(permissions);
        }
        return authorities;
    }

    @Cacheable(value = "rolePermissions", key = "#roleId")
    @Transactional(readOnly = true)
    public Collection<GrantedAuthority> getPermissionsForRole(Long roleId) {
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
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .collect(Collectors.toSet());
    }

    @CacheEvict(value = "rolePermissions", key = "#roleId")
    public void clearCacheForRole(Long roleId) {
        System.out.println("--- CACHE EVICT: Xóa cache cho roleId: " + roleId);
    }
}