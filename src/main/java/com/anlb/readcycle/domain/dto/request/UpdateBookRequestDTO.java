package com.anlb.readcycle.domain.dto.request;

import com.anlb.readcycle.utils.constant.BookStatusEnum;
import com.anlb.readcycle.utils.exception.BookChecked;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@BookChecked
public class UpdateBookRequestDTO {
    private long id;
    private String category;
    private String title;
    private String author;
    private String publisher;
    private String thumb;
    private String description;
    private BookStatusEnum status;
}
