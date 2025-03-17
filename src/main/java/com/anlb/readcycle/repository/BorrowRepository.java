package com.anlb.readcycle.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.anlb.readcycle.domain.Book;
import com.anlb.readcycle.domain.Borrow;
import com.anlb.readcycle.domain.User;
import com.anlb.readcycle.utils.constant.BorrowStatusEnum;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Long>, JpaSpecificationExecutor<Borrow> {
    Borrow findByUserAndBookAndStatus(User user, Book book, BorrowStatusEnum borrowed);
    List<Borrow> findByUserAndStatus(User user, BorrowStatusEnum borrowed);
    Long countByBook(Book book);
}
