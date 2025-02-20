package com.anlb.readcycle.dto.request;

import java.time.Instant;

import com.anlb.readcycle.domain.Book;
import com.anlb.readcycle.domain.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBorrowBookRequestDTO {
    private String status;
    private Instant borrowDate;
    private Instant dueDate;
    private Instant returnDate;
    private double fineAmount;
    private User user;
    private Book book; 
}
