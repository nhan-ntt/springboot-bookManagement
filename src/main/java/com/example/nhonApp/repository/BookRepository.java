package com.example.nhonApp.repository;

import com.example.nhonApp.dto.BookExportDTO;
import com.example.nhonApp.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query(value = "SELECT b.title, b.description, a.full_name as authorFullName " +
            "FROM books b " +
            "INNER JOIN authors a ON b.author_id = a.author_id " +
            "ORDER BY b.title", nativeQuery = true)
    List<BookExportDTO> findAllBooksForExport();
}
