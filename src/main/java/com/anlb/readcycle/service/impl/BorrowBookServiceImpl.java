package com.anlb.readcycle.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.anlb.readcycle.domain.Book;
import com.anlb.readcycle.domain.Borrow;
import com.anlb.readcycle.domain.User;
import com.anlb.readcycle.dto.request.CreateBorrowBookRequestDto;
import com.anlb.readcycle.dto.request.CreateBorrowBookRequestDto.Details;
import com.anlb.readcycle.mapper.BookMapper;
import com.anlb.readcycle.repository.BookRepository;
import com.anlb.readcycle.repository.BorrowRepository;
import com.anlb.readcycle.service.IBookService;
import com.anlb.readcycle.service.IBorrowBookService;
import com.anlb.readcycle.service.IUserService;
import com.anlb.readcycle.utils.constant.BookStatusEnum;
import com.anlb.readcycle.utils.constant.BorrowStatusEnum;
import com.anlb.readcycle.utils.exception.InvalidException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BorrowBookServiceImpl implements IBorrowBookService {

    private final IUserService userService;
    private final BookMapper bookMapper;
    private final IBookService bookService;
    private final BookRepository bookRepository;
    private final BorrowRepository borrowRepository;

    @Override
    public List<Borrow> handleBorrowBook(CreateBorrowBookRequestDto reqBorrow) throws InvalidException {
        List<Details> listBook = reqBorrow.getDetails();
        User user = userService.handleGetUserByUsername(reqBorrow.getUsername());

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

    @Override
    public Borrow handleFindBorrowByUserAndBookAndStatus(User user, Book book, BorrowStatusEnum borrowed) {
        return borrowRepository.findByUserAndBookAndStatus(user, book, borrowed);
    }

    @Override
    public List<Borrow> findByUserAndStatus(User user, BorrowStatusEnum borrowed) {
        return borrowRepository.findByUserAndStatus(user, borrowed);
    }
    
}
