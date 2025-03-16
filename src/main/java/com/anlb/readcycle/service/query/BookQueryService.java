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
    
    /**
     * Retrieves a paginated list of books that match the specified criteria.
     *
     * This method constructs a {@link Specification} based on the provided {@link BookCriteria} 
     * and fetches matching books from the database with pagination.
     *
     * @param criteria the filtering criteria containing conditions for querying books.
     * @param pageable the pagination information including page number and size.
     * @return a {@link Page} containing books that match the specified criteria.
     */
    @Transactional(readOnly = true)
    public Page<Book> findByCriteria(BookCriteria criteria, Pageable pageable) {
        log.debug("find by criteria: {}, page: {}", criteria, pageable);
        final Specification<Book> specification = createSpecification(criteria);
        return bookRepository.findAll(specification, pageable);

    }

    /**
     * Builds a {@link Specification} for filtering books based on the given criteria.
     *
     * This method dynamically constructs query conditions based on the provided
     * {@link BookCriteria}. It applies filters for title, category, and author.
     *
     * @param criteria the filtering criteria containing conditions for querying books.
     * @return a {@link Specification} representing the filtering conditions.
     */
    protected Specification<Book> createSpecification(BookCriteria criteria) {
        Specification<Book> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Book_.title));
            }

            if (criteria.getCategory() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCategory(), Book_.category));
            }

            if (criteria.getAuthor() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAuthor(), Book_.author));
            }

            if (criteria.getIsActive() != null && criteria.getIsActive().getEquals() != null) {
                Boolean isActiveValue = criteria.getIsActive().getEquals();
                specification = specification.and((root, query, cb) -> cb.equal(root.get(Book_.isActive), isActiveValue));
            }
        }

        return specification;
    }
}
