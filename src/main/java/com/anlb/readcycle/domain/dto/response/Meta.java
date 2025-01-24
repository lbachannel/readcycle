package com.anlb.readcycle.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Meta {
    /**
     * page: current page
     * pageSize: total items in current page
     * pages: total pages
     * total: total items
     */
    private int page;
    private int pageSize;
    private int pages;
    private long total;
}
