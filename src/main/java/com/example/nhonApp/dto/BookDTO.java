package com.example.nhonApp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {

    private Long id;

    @NotBlank(message = "Tiêu đề sách không được để trống")
    @Size(min = 1, max = 200, message = "Tiêu đề sách phải từ 1-200 ký tự")
    private String title;

    @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
    private String description;

    @Size(max = 50, message = "Thể loại không được vượt quá 50 ký tự")
    private String genre;

    private Long authorId;
    private String authorFullName;

    // Custom constructor for specific use cases
    public BookDTO(Long id, String title, String description, String genre, Long authorId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.authorId = authorId;
    }
}