package com.example.nhonApp.repository;

import com.example.nhonApp.entity.Author;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Author Repository - Interface để thao tác với database cho Author entity
 *
 * @Repository: Đánh dấu đây là một repository component
 * JpaRepository<Entity, ID_Type>: Cung cấp các method CRUD cơ bản
 */
@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {


    boolean existsByFullNameIgnoreCase(String fullName);

    Optional<Author> findByFullNameIgnoreCase(String fullName);
}