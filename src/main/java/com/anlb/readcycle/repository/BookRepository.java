package com.anlb.readcycle.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.anlb.readcycle.domain.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book>{
    Optional<Book> findByIdAndIsActive(long id, boolean isActive);
    List<Book> findAllByIsActive(boolean isActive);
    Book findByTitle(String title);
}
