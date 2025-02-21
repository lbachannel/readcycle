package com.anlb.readcycle.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anlb.readcycle.domain.Book;
import com.anlb.readcycle.domain.Borrow;
import com.anlb.readcycle.domain.Cart;
import com.anlb.readcycle.dto.request.CreateBorrowBookRequestDTO;
import com.anlb.readcycle.dto.response.CreateCartResponseDTO;
import com.anlb.readcycle.mapper.CartMapper;
import com.anlb.readcycle.service.BorrowBookService;
import com.anlb.readcycle.service.CartService;
import com.anlb.readcycle.utils.anotation.ApiMessage;
import com.anlb.readcycle.utils.exception.InvalidException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BorrowBookController {

    private final CartService cartService;
    private final CartMapper cartMapper;
    private final BorrowBookService borrowBookService;
    
    @PostMapping("/add-to-cart")
    @ApiMessage("Add book to cart")
    public ResponseEntity<Cart> handleAddToCart(@RequestBody Book book) throws InvalidException {
        return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(this.cartService.handleAddBookToCart(book));
    }

    @PostMapping("/borrow")
    @ApiMessage("Borrow books")
    public ResponseEntity<List<Borrow>> handleBorrowBook(@RequestBody CreateBorrowBookRequestDTO reqBorrow) throws InvalidException {

        return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(this.borrowBookService.handleBorrowBook(reqBorrow));
    }

    @GetMapping("/carts")
    @ApiMessage("Get carts by user")
    public ResponseEntity<List<CreateCartResponseDTO>> getCartsByUser() throws InvalidException {
        List<Cart> listCart = this.cartService.handleGetCartsByUser();
        List<CreateCartResponseDTO> carts = listCart.stream()
                                            .map(cart -> this.cartMapper.convertCartToCreateCartResponseDTO(cart))
                                            .collect(Collectors.toList());
        return ResponseEntity
                    .ok()
                    .body(carts);
    }

    @DeleteMapping("/carts/{id}")
    @ApiMessage("Delete cart")
    public ResponseEntity<Void> deleteCart(@PathVariable("id") long id) {
        this.cartService.handleDeleteCartById(id);
        return ResponseEntity
                    .ok()
                    .body(null);
    }
}