package com.anlb.readcycle.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anlb.readcycle.domain.Book;
import com.anlb.readcycle.domain.dto.request.CreateBookRequestDTO;
import com.anlb.readcycle.domain.dto.request.UpdateBookRequestDTO;
import com.anlb.readcycle.domain.dto.response.BookResponseDTO;
import com.anlb.readcycle.domain.dto.response.CreateBookResponseDTO;
import com.anlb.readcycle.domain.dto.response.UpdateBookResponseDTO;
import com.anlb.readcycle.repository.BookRepository;
import com.anlb.readcycle.utils.exception.InvalidException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    // handle create book
    public Book handleCreateBook(CreateBookRequestDTO requestBook) {
        Book newBook = new Book();
        newBook.setCategory(requestBook.getCategory());
        newBook.setTitle(requestBook.getTitle());
        newBook.setAuthor(requestBook.getAuthor());
        newBook.setPublisher(requestBook.getPublisher());
        newBook.setThumb(requestBook.getThumb());
        newBook.setDescription(requestBook.getDescription());
        newBook.setStatus(requestBook.getStatus());
        newBook.setActive(true);
        return this.bookRepository.save(newBook);
    }

    // convert book -> create book response dto 
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
        response.setActive(book.isActive());
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

    // Get book by id and isActive true
    public Book handleGetBookByIdAndActive(long id, boolean isActive) throws InvalidException {
        Book currentBook = this.bookRepository.findByIdAndIsActive(id, isActive).orElse(null);
        if (currentBook == null) {
            throw new InvalidException("Book with id: " + id + " does not exists");
        }
        return this.bookRepository.findByIdAndIsActive(id, isActive).get();
    }

    // handle update book
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

    // convert book -> update book response dto 
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
        response.setActive(updateBook.isActive());
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

    // convert book -> get a book response dto 
    public BookResponseDTO convertBookToBookResponseDTO(Book currentBook) {
        BookResponseDTO response = new BookResponseDTO();
        response.setId(currentBook.getId());
        response.setCategory(currentBook.getCategory());
        response.setTitle(currentBook.getTitle());
        response.setAuthor(currentBook.getAuthor());
        response.setPublisher(currentBook.getPublisher());
        response.setThumb(currentBook.getThumb());
        response.setDescription(currentBook.getDescription());
        response.setStatus(currentBook.getStatus());
        response.setActive(currentBook.isActive());
        return response;
    }

    // get all books
    public List<Book> handleGetAllBooks(boolean isActive) {
        return this.bookRepository.findAllByIsActive(isActive);
    }

    // convert books -> get books response dto 
    public List<BookResponseDTO> convertBooksToBookResponseDTO(List<Book> books) {
        List<BookResponseDTO> response = books.stream()
                                            .map(item -> new BookResponseDTO(
                                                item.getId(),
                                                item.getCategory(),
                                                item.getTitle(),
                                                item.getAuthor(),
                                                item.getPublisher(),
                                                item.getThumb(),
                                                item.getDescription(),
                                                item.getStatus(),
                                                item.isActive()
                                            ))
                                            .collect(Collectors.toList());
        return response;
    }
}
