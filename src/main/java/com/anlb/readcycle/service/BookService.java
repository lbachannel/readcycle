package com.anlb.readcycle.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anlb.readcycle.domain.Book;
import com.anlb.readcycle.domain.dto.request.CreateBookRequestDTO;
import com.anlb.readcycle.domain.dto.request.UpdateBookRequestDTO;
import com.anlb.readcycle.domain.dto.response.CreateBookResponseDTO;
import com.anlb.readcycle.domain.dto.response.UpdateBookResponseDTO;
import com.anlb.readcycle.repository.BookRepository;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book handleCreateBook(CreateBookRequestDTO requestBook) {
        Book newBook = new Book();
        newBook.setCategory(requestBook.getCategory());
        newBook.setTitle(requestBook.getTitle());
        newBook.setAuthor(requestBook.getAuthor());
        newBook.setPublisher(requestBook.getPublisher());
        newBook.setThumb(requestBook.getThumb());
        newBook.setDescription(requestBook.getDescription());
        newBook.setStatus(requestBook.getStatus());
        return this.bookRepository.save(newBook);
    }

    public CreateBookResponseDTO convertBookToCreateBookResponseDTO(Book book) {
        CreateBookResponseDTO response = new CreateBookResponseDTO();
        response.setId(book.getId());
        response.setCategory(book.getCategory());
        response.setTitle(book.getTitle());
        response.setAuthor(book.getAuthor());
        response.setPublisher(book.getPublisher());
        response.setThumb(book.getThumb());
        response.setDescription(book.getDescription());
        response.setStatus(book.getStatus());
        response.setCreatedAt(book.getCreatedAt());
        response.setCreatedBy(book.getCreatedBy());
        return response;
    }

    // Get book by id
    public Book handleGetBookById(long id) {
        if (this.bookRepository.findById(id).isPresent()) {
            return this.bookRepository.findById(id).get();
        }
        return null;
    }

    public Book handleUpdateBook(UpdateBookRequestDTO requestBook) {
        Book updateBook = this.handleGetBookById(requestBook.getId());
        if (updateBook == null) {
            return null;
        }
        updateBook.setCategory(requestBook.getCategory());
        updateBook.setTitle(requestBook.getTitle());
        updateBook.setAuthor(requestBook.getAuthor());
        updateBook.setPublisher(requestBook.getPublisher());
        updateBook.setThumb(requestBook.getThumb());
        updateBook.setDescription(requestBook.getDescription());
        updateBook.setStatus(requestBook.getStatus());
        return this.bookRepository.save(updateBook);
    }

    public UpdateBookResponseDTO convertBookToUpdateBookResponseDTO(Book updateBook) {
        UpdateBookResponseDTO response = new UpdateBookResponseDTO();
        response.setId(updateBook.getId());
        response.setCategory(updateBook.getCategory());
        response.setTitle(updateBook.getTitle());
        response.setAuthor(updateBook.getAuthor());
        response.setPublisher(updateBook.getPublisher());
        response.setThumb(updateBook.getThumb());
        response.setDescription(updateBook.getDescription());
        response.setStatus(updateBook.getStatus());
        response.setCreatedAt(updateBook.getCreatedAt());
        response.setCreatedBy(updateBook.getCreatedBy());
        response.setUpdatedAt(updateBook.getUpdatedAt());
        response.setUpdatedBy(updateBook.getUpdatedBy());
        return response;
    }

    public Book handleSoftDelete(int id) {
        Book isDeletedBook = this.handleGetBookById(id);
        if (isDeletedBook == null) {
            return null;
        }

        isDeletedBook.setActive(false);
        return this.bookRepository.save(isDeletedBook);
    }
}
