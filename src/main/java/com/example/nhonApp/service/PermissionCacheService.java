package com.example.nhonApp.service;

import com.example.nhonApp.entity.Permission;
import com.example.nhonApp.entity.RolePermission;
import com.example.nhonApp.repository.PermissionRepository;
import com.example.nhonApp.repository.RolePermissionRepository;
import io.jsonwebtoken.io.CodecException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisException;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionCacheService {

    private final RedissonClient redissonClient;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public Set<String> getPermissionNamesForRole(Long roleId) {

        // role id la cacheKey
        String cacheKey = roleId.toString();
        RMapCache<String, Set<String>> cache = null;

        try {
            cache = redissonClient.getMapCache("rolePermissions", new JsonJacksonCodec());

            // Kiểm tra cache với key là String
            if (cache.containsKey(cacheKey)) {
                log.info("--- CACHE HIT: Lấy quyền cho roleId: {}", roleId);
                return cache.get(cacheKey);
            }

        } catch (CodecException e) {
            log.error("Lỗi giải mã cache cho roleId {}: {}. Sẽ truy vấn lại từ DB.", roleId, e.getMessage());
        } catch (RedisException e) {
            log.error("Lỗi kết nối Redis khi đọc cache cho roleId {}: {}. Sẽ truy vấn lại từ DB.", roleId, e.getMessage());
        }

        log.warn("--- DATABASE QUERY: Lấy quyền cho roleId: {}", roleId);

        List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleIdIn(List.of(roleId));
        if (rolePermissions.isEmpty()) {
            tryToPutInCache(cache, cacheKey, Set.of());
            return Set.of(); // Trả về tập rỗng nếu không có quyền nào
        }

        List<Long> permissionIds = rolePermissions.stream().map(RolePermission::getPermissionId).collect(Collectors.toList());
        List<Permission> permissions = permissionRepository.findAllById(permissionIds);
        Set<String> permissionNames = permissions.stream().map(Permission::getName).collect(Collectors.toSet());

        // Lưu vào cache với key là String
        tryToPutInCache(cache, cacheKey, permissionNames);
        return permissionNames;
    }

    private void tryToPutInCache(RMapCache<String, Set<String>> cache, String key, Set<String> value) {
        if (cache == null) return;

        try {
            cache.put(key, value, 1, TimeUnit.MINUTES);
        } catch (CodecException e) {
            log.error("Lỗi mã hóa khi ghi cache cho key {}: {}", key, e.getMessage());
        } catch (RedisException e) {
            log.error("Lỗi kết nối Redis khi ghi cache cho key {}: {}", key, e.getMessage());
        }
    }
}