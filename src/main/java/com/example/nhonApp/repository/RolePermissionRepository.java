package com.example.nhonApp.repository;

import com.example.nhonApp.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermission.RolePermissionId> {
    // Tìm tất cả các quyền thuộc về một danh sách các vai trò
    List<RolePermission> findByRoleIdIn(List<Long> roleIds);
}