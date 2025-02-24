package com.anlb.readcycle.service;

import java.util.List;

import com.anlb.readcycle.domain.Book;
import com.anlb.readcycle.domain.Cart;
import com.anlb.readcycle.utils.exception.InvalidException;

public interface ICartService {
    Cart handleAddBookToCart(Book book) throws InvalidException;
    List<Cart> handleGetCartsByUser() throws InvalidException;
    void handleDeleteCartById(long id);
    void handleDeleteCarts(List<Long> ids);
}
