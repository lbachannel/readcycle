package com.anlb.readcycle.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anlb.readcycle.domain.Book;
import com.anlb.readcycle.dto.request.CreateBookRequestDto;
import com.anlb.readcycle.dto.response.BookResponseDto;
import com.anlb.readcycle.dto.response.BulkCreateResponseDto;
import com.anlb.readcycle.dto.response.ResultPaginateDto;
import com.anlb.readcycle.mapper.BookMapper;
import com.anlb.readcycle.service.IBookService;
import com.anlb.readcycle.utils.anotation.ApiMessage;
import com.anlb.readcycle.utils.exception.InvalidException;
import com.turkraft.springfilter.boot.Filter;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BookController {
    
    private final IBookService bookService;
    private final BookMapper bookMapper;

    /**
     * {@code GET  /books/{id}} : Retrieves a book by its ID.
     *
     * @param id The ID of the book to retrieve.
     * @return A {@link ResponseEntity} containing the book details in a {@link BookResponseDto}.
     * @throws InvalidException If the book is not found or is not active.
     */
    @GetMapping("/books/{id}")
    @ApiMessage("Get book by id")
    public ResponseEntity<BookResponseDto> getBookById(@PathVariable("id") long id) throws InvalidException {
        Book currentBook = bookService.handleGetBookByIdAndActive(id, true);
        return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(bookMapper.convertBookToBookResponseDto(currentBook));
    }

    /**
     * {@code GET  /books} : Retrieves a paginated list of books
     *                       based on the provided filter and pagination parameters.
     *
     * @param spec The filter specification to apply when retrieving books.
     * @param pageable The pagination information, including page number and size.
     * @return A {@link ResponseEntity} containing a paginated list of books in a {@link ResultPaginateDto}.
     */
    @GetMapping("/books")
    @ApiMessage("Get all books")
    public ResponseEntity<ResultPaginateDto> getAllBooks(@Filter Specification<Book> spec, Pageable pageable) {
        return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(bookService.handleGetAllBooksClient(spec, pageable));
    }

    /**
     * {@code POST  /books/bulk-create} : Imports multiple books in bulk.
     *
     * This endpoint allows creating multiple books at once by providing a list 
     * of book creation requests in the request body.
     *
     * @param books a list of {@link CreateBookRequestDto} objects containing book details.
     * @return a {@link ResponseEntity} containing a {@link BulkCreateResponseDto} 
     *         with the result of the bulk creation process.
     */
    @PostMapping("/books/bulk-create")
    @ApiMessage("Import books")
    public ResponseEntity<BulkCreateResponseDto> bulkCreateBooks(@RequestBody List<CreateBookRequestDto> books) {
        return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(bookService.handleBulkCreateBooksbooks(books));
    }
}
