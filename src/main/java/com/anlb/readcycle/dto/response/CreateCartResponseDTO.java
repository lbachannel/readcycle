package com.anlb.readcycle.dto.response;

import com.anlb.readcycle.domain.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class CreateCartResponseDTO {
    private long id;
    private int quantity;
    private User user;
    private Details details;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
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
