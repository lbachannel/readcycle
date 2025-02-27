package com.anlb.readcycle.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anlb.readcycle.domain.Borrow;
import com.anlb.readcycle.dto.response.ResultPaginateDto;
import com.anlb.readcycle.service.IBorrowBookService;
import com.anlb.readcycle.utils.anotation.ApiMessage;
import com.anlb.readcycle.utils.exception.InvalidException;
import com.turkraft.springfilter.boot.Filter;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BorrowHistoryController {

    private final IBorrowBookService borrowBookService;

    @GetMapping("/history")
    @ApiMessage("Get history by user")
    public ResponseEntity<ResultPaginateDto> getHistory(@Filter Specification<Borrow> spec, Pageable pageable) throws InvalidException {
        return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(borrowBookService.handleGetHistoryByUser(spec, pageable));
    }
}
