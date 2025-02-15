package com.anlb.readcycle.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anlb.readcycle.domain.Book;
import com.anlb.readcycle.dto.response.BookResponseDTO;
import com.anlb.readcycle.dto.response.ResultPaginateDTO;
import com.anlb.readcycle.mapper.BookMapper;
import com.anlb.readcycle.service.BookService;
import com.anlb.readcycle.utils.anotation.ApiMessage;
import com.anlb.readcycle.utils.exception.InvalidException;
import com.turkraft.springfilter.boot.Filter;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BookController {
    
    private final BookService bookService;
    private final BookMapper bookMapper;

    @GetMapping("/books/{id}")
    @ApiMessage("Get book by id")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable("id") long id) throws InvalidException {
        Book currentBook = this.bookService.handleGetBookByIdAndActive(id, true);
        return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(this.bookMapper.convertBookToBookResponseDTO(currentBook));
    }

    @GetMapping("/books")
    @ApiMessage("Get all books")
    public ResponseEntity<ResultPaginateDTO> getAllBooks(@Filter Specification<Book> spec, Pageable pageable) {
        return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(this.bookService.handleGetAllBooksClient(spec, pageable));
    }

}
