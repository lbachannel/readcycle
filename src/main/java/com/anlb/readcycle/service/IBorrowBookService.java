package com.anlb.readcycle.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.anlb.readcycle.domain.Book;
import com.anlb.readcycle.domain.Borrow;
import com.anlb.readcycle.domain.User;
import com.anlb.readcycle.dto.request.CreateBorrowBookRequestDto;
import com.anlb.readcycle.dto.response.ResultPaginateDto;
import com.anlb.readcycle.utils.constant.BorrowStatusEnum;
import com.anlb.readcycle.utils.exception.InvalidException;

public interface IBorrowBookService {
    List<Borrow> handleBorrowBook(CreateBorrowBookRequestDto reqBorrow) throws InvalidException;
    Borrow handleFindBorrowByUserAndBookAndStatus(User user, Book book, BorrowStatusEnum borrowed);
    List<Borrow> findByUserAndStatus(User user, BorrowStatusEnum borrowed);
    ResultPaginateDto handleGetHistoryByUser(Specification<Borrow> spec, Pageable pageable) throws InvalidException;
	Borrow handleReturnBook(Borrow borrow) throws InvalidException;
}