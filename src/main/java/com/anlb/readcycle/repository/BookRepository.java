package com.anlb.readcycle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.anlb.readcycle.domain.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {}
