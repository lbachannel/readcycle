package com.anlb.readcycle.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anlb.readcycle.domain.Book;
import com.anlb.readcycle.dto.request.CreateBookRequestDTO;
import com.anlb.readcycle.dto.request.UpdateBookRequestDTO;
import com.anlb.readcycle.dto.response.BookResponseDTO;
import com.anlb.readcycle.dto.response.ResultPaginateDTO;
import com.anlb.readcycle.mapper.BookMapper;
import com.anlb.readcycle.repository.BookRepository;
import com.anlb.readcycle.repository.specification.BookSpecifications;
import com.anlb.readcycle.utils.exception.InvalidException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    /**
     * Creates a new book record in the database.
     *
     * @param requestBook The DTO containing details of the new book.
     * @return The newly created {@link Book} entity after saving to the database.
     */
    public Book handleCreateBook(CreateBookRequestDTO requestBook) {
        Book newBook = new Book();
        newBook.setCategory(requestBook.getCategory());
        newBook.setTitle(requestBook.getTitle());
        newBook.setAuthor(requestBook.getAuthor());
        newBook.setPublisher(requestBook.getPublisher());
        newBook.setThumb(requestBook.getThumb());
        newBook.setDescription(requestBook.getDescription());
        newBook.setQuantity(requestBook.getQuantity());
        newBook.setStatus(requestBook.getStatus());
        newBook.setActive(true);
        return this.bookRepository.save(newBook);
    }

    /**
     * Retrieves a {@link Book} entity by its ID.
     *
     * @param id The unique identifier of the book.
     * @return The {@link Book} entity if found.
     * @throws InvalidException If no book with the given ID exists.
     */
    public Book handleGetBookById(long id) throws InvalidException {
        Book book = this.bookRepository.findById(id).orElse(null);
        if (book == null) {
            throw new InvalidException("Book with id: " + id + " does not exists");
        }
        return book;
    }

    /**
     * Retrieves a book by its ID and active status.
     *
     * @param id       The ID of the book to retrieve.
     * @param isActive A boolean flag indicating whether the book should be active (true) or inactive (false).
     * @return The {@link Book} entity that matches the given ID and active status.
     * @throws InvalidException if no book with the given ID and active status is found.
     */
    public Book handleGetBookByIdAndActive(long id, boolean isActive) throws InvalidException {
        Book currentBook = this.bookRepository.findByIdAndIsActive(id, isActive).orElse(null);
        if (currentBook == null) {
            throw new InvalidException("Book with id: " + id + " does not exists");
        }
        return this.bookRepository.findByIdAndIsActive(id, isActive).get();
    }

    /**
     * Updates an existing book with new details provided in the request.
     *
     * @param requestBook The DTO containing updated book information.
     * @return The updated {@link Book} entity after saving to the database.
     * @throws InvalidException if the book with the given ID does not exist.
     */
    public Book handleUpdateBook(UpdateBookRequestDTO requestBook) throws InvalidException {
        Book updateBook = this.handleGetBookById(requestBook.getId());
        updateBook.setCategory(requestBook.getCategory());
        updateBook.setTitle(requestBook.getTitle());
        updateBook.setAuthor(requestBook.getAuthor());
        updateBook.setPublisher(requestBook.getPublisher());
        updateBook.setThumb(requestBook.getThumb());
        updateBook.setDescription(requestBook.getDescription());
        updateBook.setQuantity(requestBook.getQuantity());
        updateBook.setStatus(requestBook.getStatus());
        return this.bookRepository.save(updateBook);
    }

    /**
     * Toggles the soft delete status of a book by its ID.
     *
     * If the book is currently active, it will be marked as inactive (soft deleted).
     * If the book is inactive, it will be reactivated.
     * 
     * @param id the ID of the book to toggle soft delete status
     * @return the updated {@link Book} entity with the new active status
     * @throws InvalidException if the book with the given ID does not exist
     */
    public Book handleSoftDelete(long id) throws InvalidException {
        Book isDeletedBook = this.handleGetBookById(id);
        isDeletedBook.setActive(!isDeletedBook.isActive());
        return this.bookRepository.save(isDeletedBook);
    }

    /**
     * Retrieves all books based on the given specification and pagination details.
     *
     * @param spec     The {@link Specification} used to filter books based on criteria.
     * @param pageable The {@link Pageable} object containing pagination information.
     * @return A {@link ResultPaginateDTO} containing the paginated list of books
     *         and associated metadata.
     */
    public ResultPaginateDTO handleGetAllBooks(Specification<Book> spec, Pageable pageable) {
        Page<Book> pageBook = this.bookRepository.findAll(spec, pageable);
        ResultPaginateDTO response = new ResultPaginateDTO();
        ResultPaginateDTO.Meta meta = new ResultPaginateDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageBook.getTotalPages());
        meta.setTotal(pageBook.getTotalElements());

        response.setMeta(meta);

        List<BookResponseDTO> listBook = pageBook.getContent()
                                            .stream()
                                            .map(item -> this.bookMapper.convertBookToBookResponseDTO(item))
                                            .collect(Collectors.toList());
        response.setResult(listBook);
        return response;
    }

    /**
     * Retrieves a paginated list of active books based on the given specification and pagination details.
     *
     * @param spec     The specification used to filter books.
     * @param pageable The pagination information including page number and size.
     * @return A {@link ResultPaginateDTO} containing the paginated list of books and metadata.
     */
    public ResultPaginateDTO handleGetAllBooksClient(Specification<Book> spec, Pageable pageable) {
        spec = spec.and(BookSpecifications.isActive());
        Page<Book> pageBook = this.bookRepository.findAll(spec, pageable);
        ResultPaginateDTO response = new ResultPaginateDTO();
        ResultPaginateDTO.Meta meta = new ResultPaginateDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageBook.getTotalPages());
        meta.setTotal(pageBook.getTotalElements());

        response.setMeta(meta);

        List<BookResponseDTO> listBook = pageBook.getContent()
                                            .stream()
                                            .map(item -> this.bookMapper.convertBookToBookResponseDTO(item))
                                            .collect(Collectors.toList());
        response.setResult(listBook);
        return response;
    }

    /**
     * Deletes a book from the repository by its ID.
     *
     * @param id the ID of the book to be deleted
     */
    public void handleDeleteBookById(long id) {
        this.bookRepository.deleteById(id);
    }
}
