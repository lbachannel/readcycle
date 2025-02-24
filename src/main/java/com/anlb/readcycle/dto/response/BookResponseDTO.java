package com.anlb.readcycle.dto.response;

import java.time.Instant;

import com.anlb.readcycle.utils.constant.BookStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookResponseDto {
    private long id;
    private String category;
    private String title;
    private String author;
    private String publisher;
    private String thumb;
    private String description;
    private int quantity;
    private BookStatusEnum status;
    private boolean isActive;

    private Instant createdAt;
    private String createdBy;

    private Instant updatedAt;
    private String updatedBy;
}
