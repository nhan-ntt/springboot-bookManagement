package com.example.nhonApp.controller;


import com.example.nhonApp.dto.BookDTO;
import com.example.nhonApp.entity.Author;
import com.example.nhonApp.entity.Book;
import com.example.nhonApp.response.ExportResponse;
import com.example.nhonApp.service.BookService;
import javax.validation.Valid;

import com.example.nhonApp.util.ResponseConfig;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }


    @PostMapping("/export")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InputStreamResource> export() {
        ExportResponse exportResponse = bookService.export();

        return ResponseConfig.downloadFile(exportResponse.getFilename(), exportResponse.getFileResource());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('DATA_READ')")
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        List<BookDTO> bookDTOs = books.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookDTOs);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('DATA_READ')")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) {
        Book book = bookService.getBookById(id);
        BookDTO bookDTO = convertToDTO(book);
        return ResponseEntity.ok(bookDTO);
    }


    @PostMapping
    @PreAuthorize("hasAuthority('DATA_WRITE')")
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody BookDTO bookDTO) {
        Book book = convertToEntity(bookDTO);
        Book savedBook = bookService.createBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedBook));
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('DATA_WRITE')")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long id, @Valid @RequestBody BookDTO bookDTO) {
        Book bookDetails = convertToEntity(bookDTO);
        Book updatedBook = bookService.updateBook(id, bookDetails);
        return ResponseEntity.ok(convertToDTO(updatedBook));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DATA_WRITE')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    private BookDTO convertToDTO(Book book) {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setId(book.getId());
        bookDTO.setTitle(book.getTitle());
        bookDTO.setDescription(book.getDescription());
        bookDTO.setGenre(book.getGenre());
        if (book.getAuthor() != null) {
            bookDTO.setAuthorId(book.getAuthor().getId());
            bookDTO.setAuthorFullName(book.getAuthor().getFullName());
        }
        return bookDTO;
    }

    private Book convertToEntity(BookDTO bookDTO) {
        Book book = new Book();
        book.setId(bookDTO.getId());
        book.setTitle(bookDTO.getTitle());
        book.setDescription(bookDTO.getDescription());
        book.setGenre(bookDTO.getGenre());
        if (bookDTO.getAuthorId() != null) {
            // Assuming AuthorService is available to fetch author by ID
            book.setAuthor(new Author(bookDTO.getAuthorId(), bookDTO.getAuthorFullName()));
        }
        return book;
    }

}
