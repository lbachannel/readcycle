package com.anlb.readcycle.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
public class CreateBorrowBookRequestDto {
    private String username;
    private List<Details> details;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Details {
        private long id;
        private String category;
        private String title;
        private String author;
        private String publisher;
        private String thumb;
        private int quantity;
        private boolean isActive;
        private String description;
        private String status;
    }
}
