package com.anlb.readcycle.service;

import org.springframework.stereotype.Service;

import com.anlb.readcycle.domain.Book;
import com.anlb.readcycle.domain.Cart;
import com.anlb.readcycle.domain.User;
import com.anlb.readcycle.repository.CartRepository;
import com.anlb.readcycle.utils.SecurityUtil;
import com.anlb.readcycle.utils.exception.InvalidException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

    private final UserService userService;
    private final CartRepository cartRepository;
    
    public Cart handleAddBookToCart(Book book) throws InvalidException {
        String email = SecurityUtil.getCurrentUserLogin()
                            .orElseThrow(() -> new InvalidException("Access Token invalid"));
        User user = this.userService.handleGetUserByUsername(email);
        Cart newCart = new Cart();
        if (user != null) {
            newCart.setSum(1);
            newCart.setUser(user);
            newCart.setBook(book);
            this.cartRepository.save(newCart);
        }
        return newCart;
    }
}
