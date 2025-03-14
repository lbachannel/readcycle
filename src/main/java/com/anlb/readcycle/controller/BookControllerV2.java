package com.anlb.readcycle.controller;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anlb.readcycle.dto.response.ResultPaginateDto;
import com.anlb.readcycle.service.IBookService;
import com.anlb.readcycle.service.criteria.BookCriteria;
import com.anlb.readcycle.utils.anotation.ApiMessage;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class BookControllerV2 {

    private final IBookService bookService;

    /**
     * {@code GET  /books} : Retrieves a paginated list of books based on the provided criteria.
     *
     * This endpoint allows fetching all books with optional filtering and pagination.
     *
     * @param criteria a {@link BookCriteria} object containing filters for querying books.
     * @param pageable a {@link Pageable} object defining pagination and sorting parameters.
     * @return a {@link ResponseEntity} containing a {@link ResultPaginateDto} 
     *         with the paginated list of books.
     */
    @GetMapping("/books")
    @ApiMessage("Get all books")
    public ResponseEntity<ResultPaginateDto> getAllBooks(@ParameterObject BookCriteria criteria, @ParameterObject Pageable pageable) {
        return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(bookService.handleGetAllBooksClientV2(criteria, pageable));
    }
}
