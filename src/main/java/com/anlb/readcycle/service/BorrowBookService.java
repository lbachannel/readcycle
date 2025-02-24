package com.anlb.readcycle.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.anlb.readcycle.domain.Book;
import com.anlb.readcycle.domain.Borrow;
import com.anlb.readcycle.domain.User;
import com.anlb.readcycle.dto.request.CreateBorrowBookRequestDTO;
import com.anlb.readcycle.dto.request.CreateBorrowBookRequestDTO.Details;
import com.anlb.readcycle.mapper.BookMapper;
import com.anlb.readcycle.repository.BookRepository;
import com.anlb.readcycle.repository.BorrowRepository;
import com.anlb.readcycle.utils.constant.BookStatusEnum;
import com.anlb.readcycle.utils.constant.BorrowStatusEnum;
import com.anlb.readcycle.utils.exception.InvalidException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BorrowBookService {

    private final UserService userService;
    private final BookMapper bookMapper;
    private final BookService bookService;
    private final BookRepository bookRepository;
    private final BorrowRepository borrowRepository;

    public List<Borrow> handleBorrowBook(CreateBorrowBookRequestDTO reqBorrow) throws InvalidException {
        List<Details> listBook = reqBorrow.getDetails();
        User user = this.userService.handleGetUserByUsername(reqBorrow.getUsername());

        List<Borrow> borrows = new ArrayList<>();

        for (Details bookDetails : listBook) {
            Borrow borrow = new Borrow();
            borrow.setUser(user);
            borrow.setStatus(BorrowStatusEnum.BORROWED);
            Book book = bookMapper.convertDetailsToBook(bookDetails);
            Book dbBook = bookService.handleGetBookById(book.getId());
            if (dbBook.getQuantity() == 0) {
                throw new InvalidException("Sorry the book you borrow is unavailable");
            }
            dbBook.setQuantity(dbBook.getQuantity() - 1);
            if (dbBook.getQuantity() == 0) {
                dbBook.setStatus(BookStatusEnum.UNAVAILABLE);
            }
            dbBook = bookRepository.save(dbBook);
            borrow.setBook(dbBook);
            
            borrows.add(borrow);
        }

        return borrowRepository.saveAll(borrows);
    }

    public Borrow handleFindBorrowByUserAndBookAndStatus(User user, Book book, BorrowStatusEnum borrowed) {
        return borrowRepository.findByUserAndBookAndStatus(user, book, borrowed);
    }

    public List<Borrow> findByUserAndStatus(User user, BorrowStatusEnum borrowed) {
        return borrowRepository.findByUserAndStatus(user, borrowed);
    }
    
}
