package com.anlb.readcycle.domain.dto.response;

import com.anlb.readcycle.utils.constant.BookStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookResponseDTO {
    private long id;
    private String category;
    private String title;
    private String author;
    private String publisher;
    private String thumb;
    private String description;
    private BookStatusEnum status;
    private boolean isActive;
}
