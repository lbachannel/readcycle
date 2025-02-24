package com.anlb.readcycle.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.anlb.readcycle.domain.Book;
import com.anlb.readcycle.dto.request.CreateBorrowBookRequestDto.Details;
import com.anlb.readcycle.dto.response.BookResponseDto;
import com.anlb.readcycle.dto.response.CreateBookResponseDTO;
import com.anlb.readcycle.dto.response.UpdateBookResponseDTO;

@Service
public class BookMapper {
    
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
     * Converts a {@link Book} entity to a {@link BookResponseDto}.
     *
     * @param currentBook The {@link Book} entity to be converted.
     * @return A {@link BookResponseDto} containing the book's details.
     */
    public BookResponseDto convertBookToBookResponseDTO(Book currentBook) {
        BookResponseDto response = new BookResponseDto();
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
     * Converts a list of {@link Book} objects into a list of {@link BookResponseDto} objects.
     *
     * @param books The list of {@link Book} objects to be converted.
     * @return A list of {@link BookResponseDto} objects created from the input list.
     */
    public List<BookResponseDto> convertBooksToBookResponseDTO(List<Book> books) {
        List<BookResponseDto> response = books.stream()
                                            .map(item -> new BookResponseDto(
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

    public Book convertDetailsToBook(Details details) {
        Book book = new Book();
        book.setId(details.getId());
        return book;
    }
}
