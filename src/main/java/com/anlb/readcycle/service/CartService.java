package com.anlb.readcycle.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.anlb.readcycle.domain.Book;
import com.anlb.readcycle.domain.Borrow;
import com.anlb.readcycle.domain.Cart;
import com.anlb.readcycle.domain.User;
import com.anlb.readcycle.repository.CartRepository;
import com.anlb.readcycle.utils.SecurityUtil;
import com.anlb.readcycle.utils.constant.BorrowStatusEnum;
import com.anlb.readcycle.utils.exception.InvalidException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

    private final UserService userService;
    private final CartRepository cartRepository;
    private final IBorrowBookService borrowBookService;
    
    public Cart handleAddBookToCart(Book book) throws InvalidException {
        String email = SecurityUtil.getCurrentUserLogin()
                            .orElseThrow(() -> new InvalidException("Access Token invalid"));
        User user = userService.handleGetUserByUsername(email);

        Borrow borrow = borrowBookService.handleFindBorrowByUserAndBookAndStatus(user, book, BorrowStatusEnum.BORROWED);
        if (borrow != null) {
            throw new InvalidException("Sorry, you have to return the book is borrowed before you borrow the other one.");
        }

        List<Borrow> listBorrow = borrowBookService.findByUserAndStatus(user, BorrowStatusEnum.BORROWED);
        if (!listBorrow.isEmpty()) {
            for (Borrow borrow2 : listBorrow) {
                if (book.getCategory().equals(borrow2.getBook().getCategory())) {
                    throw new InvalidException("Sorry, you have to return the book is borrowed before you borrow the other one.");
                }
            }
        }
        Cart newCart = new Cart();
        if (user != null) {
            newCart.setSum(1);
            newCart.setUser(user);
            newCart.setBook(book);
            cartRepository.save(newCart);
        }
        return newCart;
    }

    public List<Cart> handleGetCartsByUser() throws InvalidException {
        String email = SecurityUtil.getCurrentUserLogin()
                            .orElseThrow(() -> new InvalidException("Access Token invalid"));
        User user = userService.handleGetUserByUsername(email);
        if (user != null) {
            return cartRepository.findAllByUser(user);
        }
        return new ArrayList<>();
    }

    public void handleDeleteCartById(long id) {
        cartRepository.deleteById(id);
    }

    public void handleDeleteCarts(List<Long> ids) {
        cartRepository.deleteAllById(ids);
    }
}
