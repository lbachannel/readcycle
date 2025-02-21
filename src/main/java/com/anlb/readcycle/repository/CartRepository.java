package com.anlb.readcycle.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.anlb.readcycle.domain.Cart;
import com.anlb.readcycle.domain.User;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByUser(User user);
    List<Cart> findAllByUser(User user);
}
