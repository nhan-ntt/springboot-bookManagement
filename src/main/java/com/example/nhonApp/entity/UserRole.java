package com.example.nhonApp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user_roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(UserRole.UserRoleId.class)
public class UserRole {

    @Id
    private Long userId;

    @Id
    private Long roleId;

    public static class UserRoleId implements Serializable {
        private Long userId;
        private Long roleId;
    }
}
