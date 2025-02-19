package com.anlb.readcycle.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "borrow_books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "borrow_date")
    private Instant borrowDate;

    @Column(name = "due_date")
    private Instant dueDate;

    @Column(name = "return_date")
    private Instant returnDate;

    @Column(name = "fine_amount")
    private double fineAmount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
}
