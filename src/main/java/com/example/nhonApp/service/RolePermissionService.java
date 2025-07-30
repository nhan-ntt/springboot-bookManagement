package com.example.nhonApp.service;

import com.example.nhonApp.entity.Role;
import com.example.nhonApp.entity.User;
import com.example.nhonApp.entity.UserRole;
import com.example.nhonApp.repository.RoleRepository;
import com.example.nhonApp.repository.UserRepository;
import com.example.nhonApp.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolePermissionService {

    // Các repository cần thiết để lấy vai trò của user
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    // Inject service cache để lấy các permission đã được cache
    private final PermissionCacheService permissionCacheService;

    /**
     * Lấy toàn bộ quyền hạn (authorities) cho một user.
     * Phương thức này không chứa logic cache, nó chỉ điều phối.
     */
    @Transactional(readOnly = true)
    public Set<GrantedAuthority> getAuthoritiesForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Set<GrantedAuthority> authorities = new HashSet<>();

        // Lấy danh sách vai trò (roles) của user từ DB
        List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
        List<Role> roles = roleRepository.findAllById(
                userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList())
        );

        // Duyệt qua từng vai trò để lấy quyền hạn tương ứng
        for (Role role : roles) {
            // Thêm vai trò vào danh sách (ví dụ: "ROLE_ADMIN")
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));

            // Gọi service cache để lấy danh sách các permission (dạng String)
            Set<String> permissionNames = permissionCacheService.getPermissionNamesForRole(role.getId());

            // Chuyển đổi các permission String thành GrantedAuthority
            permissionNames.forEach(name -> authorities.add(new SimpleGrantedAuthority(name)));
        }

        return authorities;
    }
}