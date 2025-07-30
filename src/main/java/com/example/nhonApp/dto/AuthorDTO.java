package com.example.nhonApp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDTO {

    private Long id;

    @NotBlank(message = "Tên tác giả không được để trống")
    @Size(min = 2, max = 100, message = "Tên tác giả phải từ 2-100 ký tự")
    private String name;

    @Size(max = 500, message = "Tiểu sử tác giả không được quá 500 ký tự")
    private String biography;
}