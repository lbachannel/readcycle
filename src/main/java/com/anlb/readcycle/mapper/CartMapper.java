package com.anlb.readcycle.mapper;

import org.springframework.stereotype.Service;

import com.anlb.readcycle.domain.Book;
import com.anlb.readcycle.domain.Cart;
import com.anlb.readcycle.dto.response.CreateCartResponseDTO;
import com.anlb.readcycle.dto.response.CreateCartResponseDTO.Details;

@Service
public class CartMapper {
    public CreateCartResponseDTO convertCartToCreateCartResponseDTO(Cart cart) {
        CreateCartResponseDTO response = new CreateCartResponseDTO();
        response.setId(cart.getId());
        response.setQuantity(cart.getSum());
        response.setUser(cart.getUser());
        response.setDetails(this.convertBookToDetails(cart.getBook()));
        return response;
    }

    public Details convertBookToDetails(Book book) {
        Details details = new Details();
        details.setId(book.getId());
        details.setCategory(book.getCategory());
        details.setTitle(book.getTitle());
        details.setAuthor(book.getAuthor());
        details.setPublisher(book.getPublisher());
        details.setThumb(book.getThumb());
        details.setQuantity(book.getQuantity());
        details.setActive(book.isActive());
        details.setDescription(book.getDescription());
        details.setStatus(book.getStatus().toString());
        return details;
    }
}
