package com.example.nhonApp.service;

import com.example.nhonApp.entity.Permission;
import com.example.nhonApp.entity.Role; // Thêm import
import com.example.nhonApp.entity.RolePermission;
import com.example.nhonApp.repository.PermissionRepository;
import com.example.nhonApp.repository.RolePermissionRepository;
import com.example.nhonApp.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisException;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionCacheService {

    private final RedissonClient redissonClient;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;

    // <<< SỬA LẠI: Phương thức nhận vào roleName (String)
    @Transactional(readOnly = true)
    public Set<String> getPermissionNamesForRole(String roleName) {

        RMapCache<String, Set<String>> cache = null;
        // Key của cache bây giờ là roleName
        String cacheKey = roleName;

        try {
            cache = redissonClient.getMapCache("rolePermissionsByName", new JsonJacksonCodec());
            if (cache.containsKey(cacheKey)) {
                log.info("--- CACHE HIT: Lấy quyền cho role: {}", roleName);
                return cache.get(cacheKey);
            }
        } catch (RedisException e) {
            log.error("Lỗi kết nối Redis khi đọc cache cho role {}: {}. Sẽ truy vấn lại từ DB.", roleName, e.getMessage());
        }

        log.warn("--- DATABASE QUERY: Lấy quyền cho role: {}", roleName);

        // 1. Tìm role trong DB local để lấy ID
        Optional<Role> roleOpt = roleRepository.findByName(roleName);
        if (roleOpt.isEmpty()) {
            log.warn("Không tìm thấy role '{}' trong database local.", roleName);
            tryToPutInCache(cache, cacheKey, Set.of());
            return Set.of();
        }

        Long roleId = roleOpt.get().getId();

        // 2. Dùng roleId để tìm permissions như cũ
        List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleIdIn(List.of(roleId));
        if (rolePermissions.isEmpty()) {
            tryToPutInCache(cache, cacheKey, Set.of());
            return Set.of();
        }

        List<Long> permissionIds = rolePermissions.stream().map(RolePermission::getPermissionId).collect(Collectors.toList());
        List<Permission> permissions = permissionRepository.findAllById(permissionIds);
        Set<String> permissionNames = permissions.stream().map(Permission::getName).collect(Collectors.toSet());

        // 3. Lưu vào cache với key là roleName
        tryToPutInCache(cache, cacheKey, permissionNames);

        return permissionNames;
    }

    private void tryToPutInCache(RMapCache<String, Set<String>> cache, String key, Set<String> value) {
        if (cache == null) return;
        try {
            cache.put(key, value, 1, TimeUnit.HOURS); // Sửa lại thời gian cache hợp lý hơn
        } catch (RedisException e) {
            log.error("Lỗi kết nối Redis khi ghi cache cho key {}: {}", key, e.getMessage());
        }
    }
}