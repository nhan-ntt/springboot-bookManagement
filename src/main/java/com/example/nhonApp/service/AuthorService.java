package com.example.nhonApp.service;


import com.example.nhonApp.entity.Author;
import com.example.nhonApp.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;


    public Author createAuthor(Author author) {
        // Kiểm tra nếu author đã tồn tại
        if (authorRepository.existsByFullNameIgnoreCase(author.getFullName())) {
            throw new IllegalArgumentException("Tác giả với tên '" + author.getFullName() + "' đã tồn tại");
        }
        // Lưu author mới vào database
        return authorRepository.save(author);
    }


    @Transactional(readOnly = true)
    public List<Author> getAllAuthors() {
        // Lấy danh sách tất cả tác giả
        return authorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Author getAuthorById(Long id) {
        // Tìm kiếm tác giả theo ID
        return authorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tác giả với ID '" + id + "' không tồn tại"));
    }

    @Transactional(readOnly = true)
    public Author getAuthorByFullName(String fullName) {
        // Tìm kiếm tác giả theo tên đầy đủ
        return authorRepository.findByFullNameIgnoreCase(fullName)
                .orElseThrow(() -> new IllegalArgumentException("Tác giả với tên '" + fullName + "' không tồn tại"));
    }

    public Author updateAuthor(Long id, Author authorDetails) {
        Author existingAuthor = getAuthorById(id);

        if (!existingAuthor.getFullName().equalsIgnoreCase(authorDetails.getFullName())) {
            Optional<Author> authorWithSameName = authorRepository.findByFullNameIgnoreCase(authorDetails.getFullName());
            if (authorWithSameName.isPresent() && !authorWithSameName.get().getId().equals(id)) {
                throw new IllegalArgumentException("Tác giả với tên '" + authorDetails.getFullName() + "' đã tồn tại");
            }
        }

        if (authorDetails.getFullName() != null) {
            existingAuthor.setFullName(authorDetails.getFullName());
        }
        if (authorDetails.getBiography() != null) {
            existingAuthor.setBiography(authorDetails.getBiography());
        }        // Cập nhật thông tin tác giả
        return authorRepository.save(existingAuthor);
    }


    public void deleteAuthor(Long id) {
        Author author = getAuthorById(id);

        // Kiểm tra xem tác giả có tồn tại không
        if (!authorRepository.existsById(id)) {
            throw new IllegalArgumentException("Tác giả với ID '" + id + "' không tồn tại");
        }
        if (!author.getBooks().isEmpty()) {
            throw new IllegalStateException("Không thể xóa tác giả vì còn có sách liên quan");
        }
        // Xoá tác giả khỏi database
        authorRepository.deleteById(id);
    }
}
