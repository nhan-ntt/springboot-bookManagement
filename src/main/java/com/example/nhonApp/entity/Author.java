package com.example.nhonApp.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Author Entity - Thực thể tác giả
 *
 * @Entity: Đánh dấu class này là một JPA entity
 * @Table: Chỉ định tên bảng trong database
 */
@Entity
@Table(name = "authors")
@Data
@NoArgsConstructor
//@AllArgsConstructor
public class Author {
    public Author(Long id, String fullName, String biography) {
        this.id = id;
        this.fullName = fullName;
        this.biography = biography;
    }

    /**
     * @Id: Đánh dấu field này là primary key
     * @GeneratedValue: Tự động sinh giá trị cho primary key
     * @Column: Cấu hình chi tiết cho column
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "author_id")
    private Long id;

    /**
     * @NotBlank: Validation - field không được null, empty hoặc chỉ chứa whitespace
     * @Size: Validation - giới hạn độ dài string
     * @Column: nullable = false nghĩa là NOT NULL trong DB
     */
    @NotBlank(message = "Tên tác giả không được để trống")
    @Size(min = 2, max = 100, message = "Tên tác giả phải từ 2-100 ký tự")
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;



    @Size(max = 500, message = "Tiểu sử tác giả không được quá 500 ký tự")
    @Column(name = "biography", length = 500)
    private String biography;



    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Book> books = new ArrayList<>();

    public Author(Long id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }

    public void addBook(Book book) {
        books.add(book);
        book.setAuthor(this);
    }

    public void removeBook(Book book) {
        books.remove(book);
        book.setAuthor(null);
    }
}
