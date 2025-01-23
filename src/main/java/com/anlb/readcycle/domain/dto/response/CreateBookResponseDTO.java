package com.anlb.readcycle.domain.dto.response;

import java.time.Instant;

import com.anlb.readcycle.utils.constant.BookStatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBookResponseDTO {
    private long id;
    private String category;
    private String title;
    private String author;
    private String publisher;
    private String thumb;
    private String description;
    private BookStatusEnum status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;
    private String createdBy;
}
