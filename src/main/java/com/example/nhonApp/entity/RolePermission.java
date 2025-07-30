package com.example.nhonApp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "role_permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(RolePermission.RolePermissionId.class)
public class RolePermission {
    @Id
    private Long roleId;

    @Id
    private Long permissionId;

    // Class tĩnh cho khóa chính phức hợp
    public static class RolePermissionId implements Serializable {
        private Long roleId;
        private Long permissionId;
    }
}