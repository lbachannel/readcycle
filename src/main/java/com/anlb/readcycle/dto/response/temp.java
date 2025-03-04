package com.anlb.readcycle.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultPaginateDto {
    private Meta meta;
    private Object result;

    @Getter
    @Setter
    public static class Meta {
        private int page;
        private int pageSize;
        private int pages;
        private long total;
    }
}
