package com.anlb.readcycle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.anlb.readcycle.domain.Borrow;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Long> {
    
}
