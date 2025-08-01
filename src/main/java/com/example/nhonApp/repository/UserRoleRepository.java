package com.example.nhonApp.repository;

import com.example.nhonApp.entity.Role;
import com.example.nhonApp.entity.User;
import com.example.nhonApp.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRole.UserRoleId> {
    List<UserRole> findByUserId(Long userId);
    boolean existsByUserIdAndRoleId(Long userId, Long roleId);
}

//public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
//
//    @Query(value = "SELECT role_id FROM user_roles WHERE user_id = :userId", nativeQuery = true)
//    List<Long> findRoleIdsByUserId(@Param("userId") Long userId);
//
//    @Modifying
//    @Query(value = "DELETE FROM user_roles WHERE user_id = :userId AND role_id = :roleId", nativeQuery = true)
//    void deleteByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);
//
//    @Query(value = "SELECT DISTINCT CONCAT('ROLE_', r.name) as authority " +
//            "FROM roles r JOIN user_roles ur ON r.id = ur.role_id " +
//            "WHERE ur.user_id = :userId " +
//            "UNION " +
//            "SELECT DISTINCT p.name as authority " +
//            "FROM permissions p " +
//            "JOIN role_permissions rp ON p.id = rp.permission_id " +
//            "JOIN user_roles ur ON rp.role_id = ur.role_id " +
//            "WHERE ur.user_id = :userId",
//            nativeQuery = true)
//    List<String> findUserAuthorities(@Param("userId") Long userId);
//
//}