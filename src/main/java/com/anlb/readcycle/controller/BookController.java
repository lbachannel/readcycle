package com.anlb.readcycle.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anlb.readcycle.domain.Book;
import com.anlb.readcycle.domain.dto.request.CreateBookRequestDTO;
import com.anlb.readcycle.domain.dto.request.UpdateBookRequestDTO;
import com.anlb.readcycle.domain.dto.response.CreateBookResponseDTO;
import com.anlb.readcycle.domain.dto.response.UpdateBookResponseDTO;
import com.anlb.readcycle.service.BookService;
import com.anlb.readcycle.utils.anotation.ApiMessage;
import com.anlb.readcycle.utils.exception.InvalidException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class BookController {
    
    private final BookService bookService;
    
    public BookController(BookService bookService) {
        this.bookService = bookService;
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
        if (updateBook == null) {
            throw new InvalidException("Book with id: " + reqBook.getId() + " does not exists");
        }
        return ResponseEntity.ok(this.bookService.convertBookToUpdateBookResponseDTO(updateBook));
    }

    @DeleteMapping("/books/{id}")
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
