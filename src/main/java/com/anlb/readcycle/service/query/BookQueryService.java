package com.anlb.readcycle.service.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anlb.readcycle.domain.Book;
import com.anlb.readcycle.domain.Book_;
import com.anlb.readcycle.repository.BookRepository;
import com.anlb.readcycle.service.criteria.BookCriteria;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import tech.jhipster.service.QueryService;

@Log4j2
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookQueryService extends QueryService<Book> {

    private final BookRepository bookRepository;
    
    @Transactional(readOnly = true)
    public Page<Book> findByCriteria(BookCriteria criteria, Pageable pageable) {
        log.debug("find by criteria : {}, page: {}", criteria, pageable);
        final Specification<Book> specification = createSpecification(criteria);
        return bookRepository.findAll(specification, pageable);

    }

    protected Specification<Book> createSpecification(BookCriteria criteria) {
        Specification<Book> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getTitle() != null) {
                specification = specification.or(buildStringSpecification(criteria.getTitle(), Book_.title));
            }

            if (criteria.getCategory() != null) {
                specification = specification.or(buildStringSpecification(criteria.getCategory(), Book_.category));
            }

            if (criteria.getAuthor() != null) {
                specification = specification.or(buildStringSpecification(criteria.getAuthor(), Book_.author));
            }
        }

        return specification;
    }
}
