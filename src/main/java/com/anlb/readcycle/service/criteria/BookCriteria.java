package com.anlb.readcycle.service.criteria;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.StringFilter;

@Data
@AllArgsConstructor
public class BookCriteria implements Serializable, Criteria {
    
    StringFilter category;
    StringFilter title;
    StringFilter author;
    BooleanFilter isActive;
    Boolean isAdmin;

    public BookCriteria() {
        this.isActive = new BooleanFilter();
        this.isActive.setEquals(true); 
        this.isAdmin = false; 
    }

    public BookCriteria(BookCriteria other) {
        this.category = other.category == null ? null : other.category.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.author = other.author == null ? null : other.author.copy();
        this.isActive = other.isActive == null ? null : other.isActive.copy();
        this.isAdmin = other.isAdmin;
    }


    @Override
    public BookCriteria copy() {
        return new BookCriteria(this);
    }


    
}
