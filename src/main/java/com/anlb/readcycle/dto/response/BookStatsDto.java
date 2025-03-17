package com.anlb.readcycle.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookStatsDto {
    private String category;
    private String title;
    private long totalQty;
    private long currentQty;
    private long borrowQty;
}
