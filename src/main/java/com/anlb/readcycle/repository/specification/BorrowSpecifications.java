package com.anlb.readcycle.repository.specification;

import org.springframework.data.jpa.domain.Specification;

import com.anlb.readcycle.domain.Borrow;
import com.anlb.readcycle.domain.User;
import com.anlb.readcycle.utils.exception.InvalidException;

public class BorrowSpecifications {
    public static Specification<Borrow> getUser(User user) throws InvalidException {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("user"), user);
    }
}
