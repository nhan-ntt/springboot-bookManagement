package com.example.nhonApp.service;

import com.example.nhonApp.entity.Role;
import com.example.nhonApp.entity.User;
import com.example.nhonApp.entity.UserRole;
import com.example.nhonApp.repository.RoleRepository;
import com.example.nhonApp.repository.UserRepository;
import com.example.nhonApp.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private UserRoleRepository userRoleRepository;

    @Transactional
    public void assignRoleToUser(String username, String roleName) throws Exception {
        Long userId = userRepository.findIdByUsername(username);
        Long roleId = roleRepository.findIdByName(roleName);

        // Thay đổi ở đây: truyền ID của user và role
        if (userRoleRepository.existsByUserIdAndRoleId(userId, roleId)) {
            throw new Exception("User '" + username + "' already has the role '" + roleName + "'.");
        }

        // Thay đổi ở đây: tạo UserRole với các ID
        UserRole newUserRole = new UserRole(userId, roleId);
        userRoleRepository.save(newUserRole);
    }
}
