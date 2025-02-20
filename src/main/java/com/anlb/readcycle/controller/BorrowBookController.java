package com.anlb.readcycle.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anlb.readcycle.domain.Book;
import com.anlb.readcycle.domain.Cart;
import com.anlb.readcycle.service.CartService;
import com.anlb.readcycle.utils.anotation.ApiMessage;
import com.anlb.readcycle.utils.exception.InvalidException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BorrowBookController {

    private final CartService cartService;
    
    @PostMapping("/add-to-cart")
    @ApiMessage("Add book to cart")
    public ResponseEntity<Cart> handleAddToCart(@RequestBody Book book) throws InvalidException {
        return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(this.cartService.handleAddBookToCart(book));
    }
}

/*

    private String status;
    private Instant borrowDate;
    private Instant dueDate;
    private Instant returnDate;
    private double fineAmount;
    private User user;
    private Book book;

* */