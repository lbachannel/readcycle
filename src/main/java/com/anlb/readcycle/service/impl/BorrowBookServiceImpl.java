package com.anlb.readcycle.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anlb.readcycle.domain.Book;
import com.anlb.readcycle.domain.Borrow;
import com.anlb.readcycle.domain.User;
import com.anlb.readcycle.dto.request.CreateBorrowBookRequestDto;
import com.anlb.readcycle.dto.request.CreateBorrowBookRequestDto.Details;
import com.anlb.readcycle.dto.response.BorrowResponseDto;
import com.anlb.readcycle.dto.response.ResultPaginateDto;
import com.anlb.readcycle.mapper.BookMapper;
import com.anlb.readcycle.mapper.BorrowMapper;
import com.anlb.readcycle.repository.BookRepository;
import com.anlb.readcycle.repository.BorrowRepository;
import com.anlb.readcycle.repository.specification.BorrowSpecifications;
import com.anlb.readcycle.service.IBookService;
import com.anlb.readcycle.service.IBorrowBookService;
import com.anlb.readcycle.service.IUserService;
import com.anlb.readcycle.utils.SecurityUtil;
import com.anlb.readcycle.utils.constant.BookStatusEnum;
import com.anlb.readcycle.utils.constant.BorrowStatusEnum;
import com.anlb.readcycle.utils.exception.InvalidException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class BorrowBookServiceImpl implements IBorrowBookService {

    private final IUserService userService;
    private final BookMapper bookMapper;
    private final BorrowMapper borrowMapper;
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

    @Override
    public ResultPaginateDto handleGetHistoryByUser(Specification<Borrow> spec, Pageable pageable) throws InvalidException {
        String email = SecurityUtil.getCurrentUserLogin()
                            .orElseThrow(() -> new InvalidException("Access Token invalid"));
        User user = userService.handleGetUserByUsername(email);
        spec = spec.and(BorrowSpecifications.getUser(user));
        Page<Borrow> pageBorrow = borrowRepository.findAll(spec, pageable);
        ResultPaginateDto response = new ResultPaginateDto();
        ResultPaginateDto.Meta meta = new ResultPaginateDto.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageBorrow.getTotalPages());
        meta.setTotal(pageBorrow.getTotalElements());

        response.setMeta(meta);

        List<BorrowResponseDto> listBorrow = pageBorrow.getContent()
                                                .stream()
                                                .map(item -> borrowMapper.convertBorrowToBorrowResponseDto(item))
                                                .collect(Collectors.toList());
        response.setResult(listBorrow);
        return response;
    }

    @Override
    public Borrow handleReturnBook(Borrow borrow) throws InvalidException {
        Borrow dbBorrow = borrowRepository.findByUserAndBookAndStatus(borrow.getUser(), borrow.getBook(), borrow.getStatus());
        dbBorrow.setStatus(BorrowStatusEnum.RETURNED);
        Book dbBook = bookService.handleGetBookById(borrow.getBook().getId());
        dbBook.setQuantity(dbBook.getQuantity() + 1);
        bookRepository.save(dbBook);
        return borrow;
    }
    
}
