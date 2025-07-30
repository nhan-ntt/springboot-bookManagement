package com.example.nhonApp.service;

import com.example.nhonApp.dto.BookExportDTO;
import com.example.nhonApp.entity.Book;
import com.example.nhonApp.entity.Author;
import com.example.nhonApp.repository.BookRepository;
import com.example.nhonApp.response.ExportResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorService authorService;

    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sách với ID '" + id + "' không tồn tại"));
    }

    @Transactional
    public Book createBook(Book book) {
        // Kiểm tra author tồn tại
        if (book.getAuthor() != null && book.getAuthor().getId() != null) {
            Author author = authorService.getAuthorById(book.getAuthor().getId());
            book.setAuthor(author);
        }
        return bookRepository.save(book);
    }

    @Transactional
    public Book updateBook(Long id, Book bookDetails) {
        Book existingBook = getBookById(id);

        if (bookDetails.getTitle() != null) {
            existingBook.setTitle(bookDetails.getTitle());
        }
        if (bookDetails.getDescription() != null) {
            existingBook.setDescription(bookDetails.getDescription());
        }
        if (bookDetails.getGenre() != null) {
            existingBook.setGenre(bookDetails.getGenre());
        }
        if (bookDetails.getAuthor() != null && bookDetails.getAuthor().getId() != null) {
            Author author = authorService.getAuthorById(bookDetails.getAuthor().getId());
            existingBook.setAuthor(author);
        }

        return bookRepository.save(existingBook);
    }

    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new IllegalArgumentException("Sách với ID '" + id + "' không tồn tại");
        }
        bookRepository.deleteById(id);
    }


    // custom export method
    public ExportResponse export() {
        List<BookExportDTO> books = bookRepository.findAllBooksForExport();

        String filename = "Export thong tin sach.xlsx";

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Books");

            Row headerRow = sheet.createRow(0);

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();

            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            Cell indexHeader = headerRow.createCell(0);
            indexHeader.setCellValue("STT");
            indexHeader.setCellStyle(headerStyle);

            Cell titleHeader = headerRow.createCell(1);
            titleHeader.setCellValue("Tiêu đề");
            titleHeader.setCellStyle(headerStyle);

            Cell descriptionHeader = headerRow.createCell(2);
            descriptionHeader.setCellValue("Mô tả");
            descriptionHeader.setCellStyle(headerStyle);

            Cell authorHeader = headerRow.createCell(3);
            authorHeader.setCellValue("Tên tác giả");

            int rowNum = 1;

            for (BookExportDTO book : books) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(rowNum);
                row.createCell(1).setCellValue(book.getTitle());
                row.createCell(2).setCellValue(book.getDescription());
                row.createCell(3).setCellValue(book.getAuthorFullName());
            }

            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            InputStreamResource resource = new InputStreamResource(inputStream);

            ExportResponse exportResponse = new ExportResponse(filename, resource);

            return exportResponse;

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xuất dữ liệu: " + e.getMessage(), e);
        }
    }

}