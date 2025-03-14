package com.anlb.readcycle.service.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anlb.readcycle.domain.Role_;
import com.anlb.readcycle.domain.User;
import com.anlb.readcycle.domain.User_;
import com.anlb.readcycle.repository.UserRepository;
import com.anlb.readcycle.service.criteria.UserCriteria;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import tech.jhipster.service.QueryService;

@Log4j2
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserQueryService extends QueryService<User> {

    private final UserRepository userRepository;

    /**
     * Retrieves a paginated list of users based on the specified search criteria.
     *
     * This method constructs a {@link Specification} from the given {@link UserCriteria}
     * and executes a query to fetch users matching the criteria in a paginated format.
     *
     * @param criteria the filtering criteria for retrieving users.
     * @param pageable the pagination details.
     * @return a {@link Page} containing the users that match the criteria.
     */
    @Transactional(readOnly = true)
    public Page<User> findByCriteria(UserCriteria criteria, Pageable pageable) {
        log.debug("find by criteria : {}, page: {}", criteria, pageable);
        final Specification<User> specification = createSpecification(criteria);
        return userRepository.findAll(specification, pageable);
    }

    /**
     * Builds a {@link Specification} for filtering users based on the given criteria.
     *
     * This method dynamically constructs query conditions based on the provided
     * {@link UserCriteria}. It applies filters for name, email, date of birth, and role.
     *
     * @param criteria the filtering criteria containing conditions for querying users.
     * @return a {@link Specification} representing the filtering conditions.
     */
    protected Specification<User> createSpecification(UserCriteria criteria) {
        Specification<User> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getName() != null) {
                specification = specification.or(buildStringSpecification(criteria.getName(), User_.name));
            }

            if (criteria.getEmail() != null) {
                specification = specification.or(buildStringSpecification(criteria.getEmail(), User_.email));
            }

            if (criteria.getDateOfBirth() != null) {
                specification = specification.or(buildRangeSpecification(criteria.getDateOfBirth(), User_.dateOfBirth));
            }

            if (criteria.getRole() != null) {
                specification = specification.or(buildSpecification(criteria.getRole(), root -> root.join(User_.role).get(Role_.name)));
            }
        }
        return specification;
    }
}