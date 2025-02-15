package com.anlb.readcycle.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anlb.readcycle.domain.Book;
import com.anlb.readcycle.domain.dto.request.CreateBookRequestDTO;
import com.anlb.readcycle.domain.dto.request.UpdateBookRequestDTO;
import com.anlb.readcycle.domain.dto.response.BookResponseDTO;
import com.anlb.readcycle.domain.dto.response.CreateBookResponseDTO;
import com.anlb.readcycle.domain.dto.response.ResultPaginateDTO;
import com.anlb.readcycle.domain.dto.response.UpdateBookResponseDTO;
import com.anlb.readcycle.repository.BookRepository;
import com.anlb.readcycle.repository.specification.BookSpecifications;
import com.anlb.readcycle.utils.exception.InvalidException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

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
     * Converts a {@link Book} entity to a {@link CreateBookResponseDTO}.
     *
     * @param book The {@link Book} entity to be converted.
     * @return A {@link CreateBookResponseDTO} containing the book's details.
     */
    public CreateBookResponseDTO convertBookToCreateBookResponseDTO(Book book) {
        CreateBookResponseDTO response = new CreateBookResponseDTO();
        response.setId(book.getId());
        response.setCategory(book.getCategory());
        response.setTitle(book.getTitle());
        response.setAuthor(book.getAuthor());
        response.setPublisher(book.getPublisher());
        response.setThumb(book.getThumb());
        response.setDescription(book.getDescription());
        response.setQuantity(book.getQuantity());
        response.setStatus(book.getStatus());
        response.setActive(book.isActive());
        response.setCreatedAt(book.getCreatedAt());
        response.setCreatedBy(book.getCreatedBy());
        return response;
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
     * Converts a {@link Book} entity to an {@link UpdateBookResponseDTO}.
     *
     * @param updateBook The {@link Book} entity to be converted.
     * @return An {@link UpdateBookResponseDTO} containing the book's updated details.
     */
    public UpdateBookResponseDTO convertBookToUpdateBookResponseDTO(Book updateBook) {
        UpdateBookResponseDTO response = new UpdateBookResponseDTO();
        response.setId(updateBook.getId());
        response.setCategory(updateBook.getCategory());
        response.setTitle(updateBook.getTitle());
        response.setAuthor(updateBook.getAuthor());
        response.setPublisher(updateBook.getPublisher());
        response.setThumb(updateBook.getThumb());
        response.setDescription(updateBook.getDescription());
        response.setQuantity(updateBook.getQuantity());
        response.setStatus(updateBook.getStatus());
        response.setActive(updateBook.isActive());
        response.setCreatedAt(updateBook.getCreatedAt());
        response.setCreatedBy(updateBook.getCreatedBy());
        response.setUpdatedAt(updateBook.getUpdatedAt());
        response.setUpdatedBy(updateBook.getUpdatedBy());
        return response;
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
     * Converts a {@link Book} entity to a {@link BookResponseDTO}.
     *
     * @param currentBook The {@link Book} entity to be converted.
     * @return A {@link BookResponseDTO} containing the book's details.
     */
    public BookResponseDTO convertBookToBookResponseDTO(Book currentBook) {
        BookResponseDTO response = new BookResponseDTO();
        response.setId(currentBook.getId());
        response.setCategory(currentBook.getCategory());
        response.setTitle(currentBook.getTitle());
        response.setAuthor(currentBook.getAuthor());
        response.setPublisher(currentBook.getPublisher());
        response.setThumb(currentBook.getThumb());
        response.setDescription(currentBook.getDescription());
        response.setQuantity(currentBook.getQuantity());
        response.setStatus(currentBook.getStatus());
        response.setActive(currentBook.isActive());
        response.setCreatedAt(currentBook.getCreatedAt());
        response.setCreatedBy(currentBook.getCreatedBy());
        response.setUpdatedAt(currentBook.getUpdatedAt());
        response.setUpdatedBy(currentBook.getUpdatedBy());
        return response;
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
                                            .map(item -> this.convertBookToBookResponseDTO(item))
                                            .collect(Collectors.toList());
        response.setResult(listBook);
        return response;
    }

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
                                            .map(item -> this.convertBookToBookResponseDTO(item))
                                            .collect(Collectors.toList());
        response.setResult(listBook);
        return response;
    }


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
                                                item.getQuantity(),
                                                item.getStatus(),
                                                item.isActive(),
                                                item.getCreatedAt(),
                                                item.getCreatedBy(),
                                                item.getUpdatedAt(),
                                                item.getUpdatedBy()
                                            ))
                                            .collect(Collectors.toList());
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
