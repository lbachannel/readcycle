package com.anlb.readcycle.repository.specification;

import org.springframework.data.jpa.domain.Specification;

import com.anlb.readcycle.domain.Book;

public class BookSpecifications {
    public static Specification<Book> isActive() {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("isActive"), true);
    }


    public static Specification<Book> hasTitle(String title) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.like(root.get("title"), "%" + title + "%");
        };
    }
}
