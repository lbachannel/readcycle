package com.anlb.readcycle.service;

import com.anlb.readcycle.domain.Book;
import com.anlb.readcycle.utils.exception.InvalidException;

public interface IBookLogService {
    void logCreateBook(Book book) throws InvalidException;
    void logUpdateBook(Book oldBook, Book newBook);
    void logToggleSoftDeleteBook(long id, boolean oldActive, boolean newActive);
    void logDeleteBook(long id);
}
