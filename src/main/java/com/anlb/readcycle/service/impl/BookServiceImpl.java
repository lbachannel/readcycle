package com.anlb.readcycle.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anlb.readcycle.domain.Book;

import com.anlb.readcycle.dto.request.CreateBookRequestDto;
import com.anlb.readcycle.dto.request.UpdateBookRequestDto;
import com.anlb.readcycle.dto.response.BookResponseDto;
import com.anlb.readcycle.dto.response.BulkCreateResponseDto;
import com.anlb.readcycle.dto.response.ResultPaginateDto;
import com.anlb.readcycle.mapper.BookMapper;
import com.anlb.readcycle.repository.BookRepository;
import com.anlb.readcycle.repository.specification.BookSpecifications;
import com.anlb.readcycle.service.IBookLogService;
import com.anlb.readcycle.service.IBookService;
import com.anlb.readcycle.service.criteria.BookCriteria;
import com.anlb.readcycle.service.query.BookQueryService;
import com.anlb.readcycle.utils.exception.InvalidException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class BookServiceImpl implements IBookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final IBookLogService bookLogService;
    private final BookQueryService bookQueryService;

    /**
     * Creates a new book and logs the creation event.
     *
     * @param requestBook the DTO containing the book details.
     * @return the newly created and saved {@link Book} entity.
     * @throws InvalidException if the request is invalid.
     * @implNote This method logs the book creation event using {@code bookLogService}.
     */
    @Override
    public Book handleCreateBook(CreateBookRequestDto requestBook) throws InvalidException {
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
        newBook = bookRepository.save(newBook);
        bookLogService.logCreateBook(newBook);
        return newBook;
    }

    /**
     * Retrieves a {@link Book} entity by its ID.
     *
     * @param id The unique identifier of the book.
     * @return The {@link Book} entity if found.
     * @throws InvalidException If no book with the given ID exists.
     */
    @Override
    public Book handleGetBookById(long id) throws InvalidException {
        Book book = bookRepository.findById(id).orElse(null);
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
    @Override
    public Book handleGetBookByIdAndActive(long id, boolean isActive) throws InvalidException {
        Book currentBook = bookRepository.findByIdAndIsActive(id, isActive).orElse(null);
        if (currentBook == null) {
            throw new InvalidException("Book with id: " + id + " does not exists");
        }
        return bookRepository.findByIdAndIsActive(id, isActive).get();
    }

    /**
     * Updates an existing book's details based on the provided request data.
     * 
     * <p>This method retrieves the book by its ID, creates a clone of the original book 
     * for logging purposes, updates its attributes with new values from the request, 
     * logs the changes, and then saves the updated book to the repository.</p>
     * 
     * @param requestBook the {@code UpdateBookRequestDTO} containing the new book details
     * @return the updated {@code Book} object after saving to the repository
     * @throws InvalidException if the book with the given ID does not exist
     */
    @Override
    public Book handleUpdateBook(UpdateBookRequestDto requestBook) throws InvalidException {
        Book updateBook = handleGetBookById(requestBook.getId());
        Book oldBook = updateBook.clone();
        updateBook.setCategory(requestBook.getCategory());
        updateBook.setTitle(requestBook.getTitle());
        updateBook.setAuthor(requestBook.getAuthor());
        updateBook.setPublisher(requestBook.getPublisher());
        updateBook.setThumb(requestBook.getThumb());
        updateBook.setDescription(requestBook.getDescription());
        updateBook.setQuantity(requestBook.getQuantity());
        updateBook.setStatus(requestBook.getStatus());
        bookLogService.logUpdateBook(oldBook, updateBook);
        return bookRepository.save(updateBook);
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
    @Override
    public Book handleSoftDelete(long id) throws InvalidException {
        Book isDeletedBook = handleGetBookById(id);
        boolean oldActive = isDeletedBook.isActive();
        isDeletedBook.setActive(!isDeletedBook.isActive());
        bookLogService.logToggleSoftDeleteBook(isDeletedBook.getId(), oldActive, isDeletedBook.isActive());
        return bookRepository.save(isDeletedBook);
    }

    /**
     * Retrieves all books based on the given specification and pagination details.
     *
     * @param spec     The {@link Specification} used to filter books based on criteria.
     * @param pageable The {@link Pageable} object containing pagination information.
     * @return A {@link ResultPaginateDto} containing the paginated list of books
     *         and associated metadata.
     */
    @Override
    public ResultPaginateDto handleGetAllBooks(Specification<Book> spec, Pageable pageable) {
        Page<Book> pageBook = bookRepository.findAll(spec, pageable);
        ResultPaginateDto response = new ResultPaginateDto();
        ResultPaginateDto.Meta meta = new ResultPaginateDto.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageBook.getTotalPages());
        meta.setTotal(pageBook.getTotalElements());

        response.setMeta(meta);

        List<BookResponseDto> listBook = pageBook.getContent()
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
     * @return A {@link ResultPaginateDto} containing the paginated list of books and metadata.
     */
    @Override
    public ResultPaginateDto handleGetAllBooksClient(Specification<Book> spec, Pageable pageable) {
        spec = spec.and(BookSpecifications.isActive());
        Page<Book> pageBook = bookRepository.findAll(spec, pageable);
        ResultPaginateDto response = new ResultPaginateDto();
        ResultPaginateDto.Meta meta = new ResultPaginateDto.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageBook.getTotalPages());
        meta.setTotal(pageBook.getTotalElements());

        response.setMeta(meta);

        List<BookResponseDto> listBook = pageBook.getContent()
                                            .stream()
                                            .map(item -> bookMapper.convertBookToBookResponseDTO(item))
                                            .collect(Collectors.toList());
        response.setResult(listBook);
        return response;
    }

    @Override
    public ResultPaginateDto handleGetAllBooksClientV2(BookCriteria bookCriteria, Pageable pageable) {
        Page<Book> pageBook = bookQueryService.findByCriteria(bookCriteria, pageable);
        ResultPaginateDto response = new ResultPaginateDto();
        ResultPaginateDto.Meta meta = new ResultPaginateDto.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageBook.getTotalPages());
        meta.setTotal(pageBook.getTotalElements());

        response.setMeta(meta);
        List<BookResponseDto> listBook = pageBook.getContent()
                                                .stream()
                                                .map(item -> bookMapper.convertBookToBookResponseDTO(item))
                                                .collect(Collectors.toList());
        response.setResult(listBook);
        return response;
    }

    /**
     * Deletes a book from the repository by its ID.
     *
     * This method first logs the deletion activity using {@code bookLogService}
     *              then proceeds to remove the book from the repository.
     * @param id the ID of the book to be deleted
     */
    @Override
    public void handleDeleteBookById(long id) {
        bookLogService.logDeleteBook(id);
        bookRepository.deleteById(id);
    }

    @Override
    public BulkCreateResponseDto handleBulkCreateBooksbooks(List<CreateBookRequestDto> books) {
        int countSuccess = 0;
        int countError = 0;
        for (CreateBookRequestDto book : books) {
            try {
                Book dbBook = handleGetBookByTitle(book.getTitle());
                if (dbBook != null) {
                    countError++;
                } else {
                    handleCreateBook(book);
                    countSuccess++;
                }
            } catch (Exception e) {
                countError++;
            }
        }
        return new BulkCreateResponseDto(countSuccess, countError);
    }

    @Override
    public Book handleGetBookByTitle(String title) {
        return bookRepository.findByTitle(title);
    }
}
