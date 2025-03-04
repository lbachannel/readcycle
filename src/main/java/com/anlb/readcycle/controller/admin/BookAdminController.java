package com.anlb.readcycle.controller.admin;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anlb.readcycle.domain.Book;
import com.anlb.readcycle.dto.request.CreateBookRequestDto;
import com.anlb.readcycle.dto.request.UpdateBookRequestDto;
import com.anlb.readcycle.dto.response.CreateBookResponseDto;
import com.anlb.readcycle.dto.response.ResultPaginateDto;
import com.anlb.readcycle.dto.response.UpdateBookResponseDto;
import com.anlb.readcycle.mapper.BookMapper;
import com.anlb.readcycle.service.IBookService;
import com.anlb.readcycle.utils.anotation.ApiMessage;
import com.anlb.readcycle.utils.exception.InvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class BookAdminController {

    private final IBookService bookService;
    private final BookMapper bookMapper;

    /**
     * {@code GET  /books} : get all books.
     *
     * @param spec     The specification used for filtering the books.
     * @param pageable The pagination information.
     * @return A {@link ResponseEntity} containing a {@link ResultPaginateDto} with the list of books.
     */
    @GetMapping("/books")
    @ApiMessage("Get all books")
    public ResponseEntity<ResultPaginateDto> getAllBooks(@Filter Specification<Book> spec, Pageable pageable) {
        return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(bookService.handleGetAllBooks(spec, pageable));
    }

    /**
     * {@code POST  /books}  : Creates a new book.
     *
     * @param reqBook the request body containing book details.
     * @return a {@link ResponseEntity} with the created {@link CreateBookResponseDto}.
     * @throws InvalidException if the book creation process encounters an error.
     * 
     * @implNote This method delegates book creation to {@code bookService} and 
     *           converts the created book to a response Dto using {@code bookMapper}.
     */
    @PostMapping("/books")
    @ApiMessage("Create a book")
    public ResponseEntity<CreateBookResponseDto> createNewBook(@Valid @RequestBody CreateBookRequestDto reqBook) throws InvalidException {
        Book newBook = bookService.handleCreateBook(reqBook);
        return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(bookMapper.convertBookToCreateBookResponseDto(newBook));
    }

    /**
     * {@code PUT  /books}  : Updates an existing Book.
     * 
     * @param reqBook The request body containing the updated book details.
     * @return A {@link ResponseEntity} containing an {@link UpdateBookResponseDto} with the updated book details.
     * @throws InvalidException If the update request is invalid.
     */
    @PutMapping("/books")
    @ApiMessage("Update book")
    public ResponseEntity<UpdateBookResponseDto> updateBook(@RequestBody UpdateBookRequestDto reqBook) throws InvalidException {
        Book updateBook = bookService.handleUpdateBook(reqBook);
        return ResponseEntity
                    .ok(bookMapper.convertBookToUpdateBookResponseDto(updateBook));
    }

    /**
     * {@code PUT  /books/{id}}  : Toggles the soft delete status of a book by its ID.
     * 
     * @param id The ID of the book to be toggled.
     * @return A {@link ResponseEntity} containing the updated {@link Book} with its new soft delete status.
     * @throws InvalidException If the book with the given ID is not found or the operation is invalid.
     */
    @PutMapping("/books/{id}")
    @ApiMessage("Toggle soft delete a book")
    public ResponseEntity<Book> toggleSoftDeleteBook(@PathVariable("id") int id) throws InvalidException {
        Book isDeletedBook = bookService.handleGetBookById(id);
        isDeletedBook = bookService.handleSoftDelete(isDeletedBook.getId());
        return ResponseEntity
                    .ok()
                    .body(isDeletedBook);
    }

    /**
     * {@code DELETE /books/{id}} : Deletes a book by its ID.
     *
     * @return A {@link ResponseEntity} containing the deleted {@link Book}.
     * @throws InvalidException If the book with the given ID is not found or the operation is invalid.
     */
    @DeleteMapping("/books/{id}")
    @ApiMessage("Delete a book")
    public ResponseEntity<Book> deleteBook(@PathVariable("id") long id) throws InvalidException {
        Book book = bookService.handleGetBookById(id);
        bookService.handleDeleteBookById(book.getId());
        return ResponseEntity.ok().body(book);
    }
}
