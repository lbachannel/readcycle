package com.anlb.readcycle.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.anlb.readcycle.domain.Book;
import com.anlb.readcycle.dto.request.CreateBookRequestDto;
import com.anlb.readcycle.dto.request.UpdateBookRequestDto;
import com.anlb.readcycle.dto.response.BulkCreateResponseDto;
import com.anlb.readcycle.dto.response.ResultPaginateDto;
import com.anlb.readcycle.utils.exception.InvalidException;

public interface IBookService {
    Book handleCreateBook(CreateBookRequestDto requestBook) throws InvalidException;
    Book handleGetBookById(long id) throws InvalidException;
    Book handleGetBookByIdAndActive(long id, boolean isActive) throws InvalidException;
    Book handleUpdateBook(UpdateBookRequestDto requestBook) throws InvalidException;
    Book handleSoftDelete(long id) throws InvalidException;
    ResultPaginateDto handleGetAllBooks(Specification<Book> spec, Pageable pageable);
    ResultPaginateDto handleGetAllBooksClient(Specification<Book> spec, Pageable pageable);
    void handleDeleteBookById(long id);
    BulkCreateResponseDto handleBulkCreateBooksbooks(List<CreateBookRequestDto> books);
    Book handleGetBookByTitle(String title);
}
