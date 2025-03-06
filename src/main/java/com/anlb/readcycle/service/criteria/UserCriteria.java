package com.anlb.readcycle.service.criteria;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.LocalDateFilter;
import tech.jhipster.service.filter.StringFilter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCriteria implements Serializable, Criteria {
    StringFilter name;
    StringFilter email;
    LocalDateFilter dateOfBirth;
    StringFilter role;

    public UserCriteria(UserCriteria other) {
        this.name = other.name == null ? null : other.name.copy();
        this.email = other.email == null ? null : other.email.copy();
        this.dateOfBirth = other.dateOfBirth == null ? null : other.dateOfBirth.copy();
        this.role = other.role == null ? null : other.role.copy();
    }

    @Override
    public UserCriteria copy() {
        return new UserCriteria(this);
    }
}
