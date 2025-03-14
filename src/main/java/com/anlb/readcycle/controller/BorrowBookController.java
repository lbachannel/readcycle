package com.anlb.readcycle.controller;

import java.util.List;
import java.util.stream.Collectors;

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
import com.anlb.readcycle.domain.Borrow;
import com.anlb.readcycle.domain.Cart;
import com.anlb.readcycle.dto.request.CreateBorrowBookRequestDto;
import com.anlb.readcycle.dto.response.CreateCartResponseDto;
import com.anlb.readcycle.mapper.CartMapper;
import com.anlb.readcycle.service.IBorrowBookService;
import com.anlb.readcycle.service.ICartService;
import com.anlb.readcycle.utils.anotation.ApiMessage;
import com.anlb.readcycle.utils.exception.InvalidException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BorrowBookController {

    private final ICartService cartService;
    private final CartMapper cartMapper;
    private final IBorrowBookService borrowBookService;

    /**
     * {@code POST  /add-to-cart} : Adds a book to the user's cart.
     *
     * This endpoint allows adding a book to the shopping cart by providing
     * the book details in the request body.
     *
     * @param book a {@link Book} object representing the book to be added to the
     *             cart.
     * @return a {@link ResponseEntity} containing the updated {@link Cart}
     *         after adding the book.
     * @throws InvalidException if the book cannot be added to the cart due to
     *                          validation errors.
     */
    @PostMapping("/add-to-cart")
    @ApiMessage("Add book to cart")
    public ResponseEntity<Cart> handleAddToCart(@RequestBody Book book) throws InvalidException {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cartService.handleAddBookToCart(book));
    }

    /**
     * {@code POST  /borrow} : Borrows books from the library.
     *
     * This endpoint allows users to borrow one or more books by providing
     * the necessary details in the request body.
     *
     * @param reqBorrow a {@link CreateBorrowBookRequestDto} containing the book
     *                  borrowing details.
     * @return a {@link ResponseEntity} containing a list of {@link Borrow}
     *         objects representing the borrowed books.
     * @throws InvalidException if the borrowing request is invalid or cannot be
     *                          processed.
     */
    @PostMapping("/borrow")
    @ApiMessage("Borrow books")
    public ResponseEntity<List<Borrow>> handleBorrowBook(@RequestBody CreateBorrowBookRequestDto reqBorrow)
            throws InvalidException {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(borrowBookService.handleBorrowBook(reqBorrow));
    }

    /**
     * {@code PUT  /return-book} : Returns borrowed books to the library.
     *
     * This endpoint allows users to return borrowed books by providing
     * the borrowing details in the request body.
     *
     * @param borrow a {@link Borrow} object containing the details of the borrowed
     *               books to be returned.
     * @return a {@link ResponseEntity} containing the result of the return process.
     * @throws InvalidException if the return request is invalid or cannot be
     *                          processed.
     */
    @PutMapping("/return-book")
    @ApiMessage("Return books")
    public ResponseEntity<?> handleReturnBook(@RequestBody Borrow borrow) throws InvalidException {
        return ResponseEntity
                .ok(borrowBookService.handleReturnBook(borrow));
    }

    /**
     * {@code GET  /carts} : Retrieves the shopping carts of the authenticated user.
     *
     * This endpoint fetches all carts associated with the currently logged-in user.
     *
     * @return a {@link ResponseEntity} containing a list of {@link CreateCartResponseDto} 
     *         representing the user's carts.
     * @throws InvalidException if the request is invalid or encounters an issue.
     */
    @GetMapping("/carts")
    @ApiMessage("Get carts by user")
    public ResponseEntity<List<CreateCartResponseDto>> getCartsByUser() throws InvalidException {
        List<Cart> listCart = cartService.handleGetCartsByUser();
        List<CreateCartResponseDto> carts = listCart.stream()
                .map(cart -> cartMapper.convertCartToCreateCartResponseDto(cart))
                .collect(Collectors.toList());
        return ResponseEntity
                .ok()
                .body(carts);
    }

    /**
     * {@code DELETE  /carts/{id}} : Deletes a cart by its ID.
     *
     * This endpoint allows users to delete a specific cart by providing its ID.
     *
     * @param id the ID of the cart to be deleted.
     * @return a {@link ResponseEntity} with an HTTP status indicating the result of the deletion.
     */
    @DeleteMapping("/carts/{id}")
    @ApiMessage("Delete cart")
    public ResponseEntity<Void> deleteCart(@PathVariable("id") long id) {
        cartService.handleDeleteCartById(id);
        return ResponseEntity
                .ok()
                .body(null);
    }
}