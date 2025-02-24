package com.anlb.readcycle.service;

import com.anlb.readcycle.domain.User;
import com.anlb.readcycle.utils.exception.InvalidException;

public interface IUserLogService {
    void logCreateUser(User user, User userLogin) throws InvalidException;
    void logUpdateUser(User oldUser, User newUser, User userLogin);
    void logDeleteUser(long id, User userLogin);
}
