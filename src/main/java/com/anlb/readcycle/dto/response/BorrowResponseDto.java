package com.anlb.readcycle.dto.response;

import java.time.Instant;

import com.anlb.readcycle.domain.Book;
import com.anlb.readcycle.domain.User;
import com.anlb.readcycle.utils.constant.BorrowStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BorrowResponseDto {
    private long id;
    private BorrowStatusEnum status;
    private Book book;
    private User user;

    private Instant createdAt;
    private String createdBy;

    private Instant updatedAt;
    private String updatedBy;
}
