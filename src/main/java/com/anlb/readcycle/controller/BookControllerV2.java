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

    @GetMapping("/books")
    @ApiMessage("Get all books")
    public ResponseEntity<ResultPaginateDto> getAllBooks(@ParameterObject BookCriteria criteria, @ParameterObject Pageable pageable) {
        return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(bookService.handleGetAllBooksClientV2(criteria, pageable));
    }
}
