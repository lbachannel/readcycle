package com.anlb.readcycle.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anlb.readcycle.domain.Book;
import com.anlb.readcycle.domain.dto.request.CreateBookRequestDTO;
import com.anlb.readcycle.domain.dto.request.UpdateBookRequestDTO;
import com.anlb.readcycle.domain.dto.response.BookResponseDTO;
import com.anlb.readcycle.domain.dto.response.CreateBookResponseDTO;
import com.anlb.readcycle.domain.dto.response.UpdateBookResponseDTO;
import com.anlb.readcycle.service.BookService;
import com.anlb.readcycle.utils.anotation.ApiMessage;
import com.anlb.readcycle.utils.exception.InvalidException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BookController {
    
    private final BookService bookService;

    @GetMapping("/books/{id}")
    @ApiMessage("Get book by id")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable("id") long id) throws InvalidException {
        Book currentBook = this.bookService.handleGetBookByIdAndActive(id, true);
        return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(this.bookService.convertBookToBookResponseDTO(currentBook));
    }

    @GetMapping("/books")
    @ApiMessage("Get books")
    public ResponseEntity<List<BookResponseDTO>> getBooks() {
        List<Book> books = this.bookService.handleGetAllBooks(true);
        return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(this.bookService.convertBooksToBookResponseDTO(books));
    }

    @PostMapping("/books")
    @ApiMessage("Create a book")
    public ResponseEntity<CreateBookResponseDTO> createNewBook(@Valid @RequestBody CreateBookRequestDTO reqBook) {
        Book newBook = this.bookService.handleCreateBook(reqBook);
        return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(this.bookService.convertBookToCreateBookResponseDTO(newBook));
    }

    @PutMapping("/books")
    @ApiMessage("Update book")
    public ResponseEntity<UpdateBookResponseDTO> updateBook(@RequestBody UpdateBookRequestDTO reqBook) throws InvalidException {
        Book updateBook = this.bookService.handleUpdateBook(reqBook);
        return ResponseEntity
                    .ok(this.bookService.convertBookToUpdateBookResponseDTO(updateBook));
    }

    @PutMapping("/books/{id}")
    @ApiMessage("Delete book")
    public ResponseEntity<Void> deleteBook(@PathVariable("id") int id) throws InvalidException {
        Book isDeletedBook = this.bookService.handleGetBookById(id);
        if (isDeletedBook == null) {
            throw new InvalidException("Book with id: " + id + " does not exists");
        }
        this.bookService.handleSoftDelete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
