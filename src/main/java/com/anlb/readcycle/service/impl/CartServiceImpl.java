package com.anlb.readcycle.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.anlb.readcycle.domain.Book;
import com.anlb.readcycle.domain.Borrow;
import com.anlb.readcycle.domain.Cart;
import com.anlb.readcycle.domain.User;
import com.anlb.readcycle.repository.CartRepository;
import com.anlb.readcycle.service.IBorrowBookService;
import com.anlb.readcycle.service.ICartService;
import com.anlb.readcycle.service.IUserService;
import com.anlb.readcycle.utils.SecurityUtil;
import com.anlb.readcycle.utils.constant.BorrowStatusEnum;
import com.anlb.readcycle.utils.exception.InvalidException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements ICartService {

    private final IUserService userService;
    private final CartRepository cartRepository;
    private final IBorrowBookService borrowBookService;
    
    /**
     * Adds a book to the user's cart.
     *
     * This method checks if the user has already borrowed the same book or a book of the same category
     * before allowing them to add a new book to the cart. If any such condition is met, an exception is thrown.
     *
     * @param book the {@link Book} to be added to the cart.
     * @return the newly created {@link Cart} entity.
     * @throws InvalidException if the user is not authenticated, has already borrowed the same book, 
     *         or has borrowed another book of the same category.
     */
    @Override
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

    /**
     * Retrieves the list of carts associated with the currently authenticated user.
     *
     * This method fetches the authenticated user's information and returns all cart items belonging to them.
     * If the user is not authenticated, an {@link InvalidException} is thrown.
     *
     * @return a list of {@link Cart} objects associated with the user.
     * @throws InvalidException if the user is not authenticated (invalid access token).
     */
    @Override
    public List<Cart> handleGetCartsByUser() throws InvalidException {
        String email = SecurityUtil.getCurrentUserLogin()
                            .orElseThrow(() -> new InvalidException("Access Token invalid"));
        User user = userService.handleGetUserByUsername(email);
        if (user != null) {
            return cartRepository.findAllByUser(user);
        }
        return new ArrayList<>();
    }

    /**
     * Deletes a cart by its ID.
     *
     * This method removes the cart entry from the database based on the provided cart ID.
     * If the cart does not exist, no action is taken.
     *
     * @param id the unique identifier of the cart to be deleted.
     */
    public void handleDeleteCartById(long id) {
        cartRepository.deleteById(id);
    }

    /**
     * Deletes multiple carts by their IDs.
     *
     * This method removes all cart entries from the database that match the provided list of IDs.
     * If any ID does not exist, no action is taken for that specific ID.
     *
     * @param ids the list of unique identifiers of the carts to be deleted.
     */
    public void handleDeleteCarts(List<Long> ids) {
        cartRepository.deleteAllById(ids);
    }
}
