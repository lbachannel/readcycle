package com.anlb.readcycle.service;

import java.util.List;

import com.anlb.readcycle.domain.Book;
import com.anlb.readcycle.domain.Borrow;
import com.anlb.readcycle.domain.User;
import com.anlb.readcycle.dto.request.CreateBorrowBookRequestDto;
import com.anlb.readcycle.utils.constant.BorrowStatusEnum;
import com.anlb.readcycle.utils.exception.InvalidException;

public interface IBorrowBookService {
    List<Borrow> handleBorrowBook(CreateBorrowBookRequestDto reqBorrow) throws InvalidException;
    Borrow handleFindBorrowByUserAndBookAndStatus(User user, Book book, BorrowStatusEnum borrowed);
    List<Borrow> findByUserAndStatus(User user, BorrowStatusEnum borrowed);
}