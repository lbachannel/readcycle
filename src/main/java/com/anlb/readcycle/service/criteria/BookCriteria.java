package com.anlb.readcycle.service.criteria;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.StringFilter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookCriteria implements Serializable, Criteria {
    
    StringFilter category;
    StringFilter title;
    StringFilter author;

    public BookCriteria(BookCriteria other) {
        this.category = other.category == null ? null : other.category.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.author = other.author == null ? null : other.author.copy();
    }


    @Override
    public BookCriteria copy() {
        return new BookCriteria(this);
    }


    
}
