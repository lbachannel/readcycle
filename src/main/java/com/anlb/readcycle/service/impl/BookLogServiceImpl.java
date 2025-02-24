package com.anlb.readcycle.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.anlb.readcycle.domain.Book;
import com.anlb.readcycle.domain.User;
import com.anlb.readcycle.dto.activitylog.ActivityDescription;
import com.anlb.readcycle.dto.activitylog.ActivityGroup;
import com.anlb.readcycle.dto.activitylog.ActivityLog;
import com.anlb.readcycle.dto.activitylog.ActivityType;
import com.anlb.readcycle.service.ActivityLogService;
import com.anlb.readcycle.service.IBookLogService;
import com.anlb.readcycle.service.IUserService;
import com.anlb.readcycle.utils.SecurityUtil;
import com.anlb.readcycle.utils.exception.InvalidException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookLogServiceImpl implements IBookLogService {

    private final ActivityLogService activityLogService;
    private final IUserService userService;

    /**
     * Logs the creation of a new book, capturing its details as an activity log.
     *
     * @param book The {@link Book} object containing details of the created book.
     * @throws InvalidException If the current user's authentication token is invalid.
     */
    @Override
    public void logCreateBook(Book book) throws InvalidException {
        String email = SecurityUtil.getCurrentUserLogin()
                            .orElseThrow(() -> new InvalidException("Access Token invalid"));
        User user = userService.handleGetUserByUsername(email);
        try {
            List<ActivityDescription> descriptions = new ArrayList<>();
            descriptions.add(ActivityDescription.from("bookId", String.valueOf(book.getId()), "Book id"));
            if (!StringUtils.isBlank(book.getCategory())) {
                descriptions.add(ActivityDescription.from("category", book.getCategory(), "Category"));
            }
            if (!StringUtils.isBlank(book.getTitle())) {
                descriptions.add(ActivityDescription.from("title", book.getTitle(), "Title"));
            }
            if (!StringUtils.isBlank(book.getAuthor())) {
                descriptions.add(ActivityDescription.from("author", book.getAuthor(), "Author"));
            }
            if (!StringUtils.isBlank(book.getPublisher())) {
                descriptions.add(ActivityDescription.from("publisher", book.getPublisher(), "Publisher"));
            }
            if (!StringUtils.isBlank(book.getThumb())) {
                descriptions.add(ActivityDescription.from("thumb", book.getThumb(), "Thumb"));
            }
            if (book.getQuantity() != 0) {
                descriptions.add(ActivityDescription.from("quantity", String.valueOf(book.getQuantity()), "Quantity"));
            }
            if (book.getStatus() != null && !StringUtils.isBlank(String.valueOf(book.getStatus()))) {
                descriptions.add(ActivityDescription.from("status", String.valueOf(book.getStatus()), "Status"));
            }
            descriptions.add(ActivityDescription.from("isActive", book.isActive() ? "True" : "False", "Active"));

            ActivityLog activityLog = ActivityLog.formatLogMessage(ActivityGroup.BOOK, ActivityType.CREATE_BOOK, descriptions);
            activityLogService.log(user, activityLog);
        } catch (Exception e) {
            log.error("logging activity error: ", e);
        }
    }

    /**
     * Logs the changes made to a book's details by comparing the old and new book objects.
     * 
     * @param oldBook the original {@code Book} object before updates
     * @param newBook the updated {@code Book} object
     * @throws InvalidException if the current user cannot be retrieved from the security context
     */
    @Override
    public void logUpdateBook(Book oldBook, Book newBook) {
        try {
            List<ActivityDescription> descriptions = new ArrayList<>();
            descriptions.add(ActivityDescription.from("bookId", String.valueOf(newBook.getId()), "Book id"));
            
            if (!StringUtils.equals(oldBook.getCategory(), newBook.getCategory())) {
                descriptions.add(ActivityDescription.from("category", oldBook.getCategory() + " → " + newBook.getCategory(), "Category"));
            }

            if (!StringUtils.equals(oldBook.getTitle(), newBook.getTitle())) {
                descriptions.add(ActivityDescription.from("title", oldBook.getTitle() + " → " + newBook.getTitle(), "Title"));
            }

            if (!StringUtils.equals(oldBook.getAuthor(), newBook.getAuthor())) {
                descriptions.add(ActivityDescription.from("author", oldBook.getAuthor() + " → " + newBook.getAuthor(), "Author"));
            }

            if (!StringUtils.equals(oldBook.getPublisher(), newBook.getPublisher())) {
                descriptions.add(ActivityDescription.from("publisher", oldBook.getPublisher() + " → " + newBook.getPublisher(), "Publisher"));
            }

            if (!StringUtils.equals(oldBook.getThumb(), newBook.getThumb())) {
                if (StringUtils.isBlank(oldBook.getThumb())) {
                    descriptions.add(ActivityDescription.from("thumb", "none" + " → " + newBook.getThumb(), "Thumb"));
                } else if (StringUtils.isBlank(newBook.getThumb())) {
                    descriptions.add(ActivityDescription.from("thumb", oldBook.getThumb() + " → " + "none", "Thumb"));
                } else {
                    descriptions.add(ActivityDescription.from("thumb", oldBook.getThumb() + " → " + newBook.getThumb(), "Thumb"));
                }
            }

            if (oldBook.getQuantity() != newBook.getQuantity()) {
                descriptions.add(ActivityDescription.from("quantity", oldBook.getQuantity() + " → " + newBook.getQuantity(), "Quantity"));
            }

            if (oldBook.isActive() != newBook.isActive()) {
                descriptions.add(ActivityDescription.from("isActive", (oldBook.isActive() ? "True" : "False")  + " → " + (newBook.isActive() ? "True" : "False"), "Active"));
            }

            if (!StringUtils.equals(String.valueOf(oldBook.getStatus()), String.valueOf(newBook.getStatus()))) {
                descriptions.add(ActivityDescription.from("status", String.valueOf(oldBook.getStatus()) + " → " + String.valueOf(newBook.getStatus()), "Status"));
            }

            if (descriptions.size() > 1) {
                String email = SecurityUtil.getCurrentUserLogin()
                            .orElseThrow(() -> new InvalidException("Access Token invalid"));
                User user = userService.handleGetUserByUsername(email);
                ActivityLog activityLog = ActivityLog.formatLogMessage(ActivityGroup.BOOK, ActivityType.UPDATE_BOOK, descriptions);
                activityLogService.log(user, activityLog);
            }

        } catch (Exception e) {
            log.error("logging activity error: ", e);
        }
    }

    /**
     * Logs an activity when a book's soft delete status is toggled.
     *
     * @param id        the unique identifier of the book
     * @param oldActive the previous active status of the book (true if active, false if inactive)
     * @param newActive the new active status of the book (true if active, false if inactive)
     * @throws InvalidException if the current user's access token is invalid
     */
    @Override
    public void logToggleSoftDeleteBook(long id, boolean oldActive, boolean newActive) {
        try {
            List<ActivityDescription> descriptions = new ArrayList<>();
            descriptions.add(ActivityDescription.from("bookId", String.valueOf(id), "Book id"));
            descriptions.add(ActivityDescription.from("isActive", (oldActive ? "True" : "False")  + " → " + (newActive ? "True" : "False"), "Active"));
            String email = SecurityUtil.getCurrentUserLogin()
                            .orElseThrow(() -> new InvalidException("Access Token invalid"));
            User user = userService.handleGetUserByUsername(email);
            ActivityLog activityLog = ActivityLog.formatLogMessage(ActivityGroup.BOOK, ActivityType.SOFT_DELETE_BOOK, descriptions);
            activityLogService.log(user, activityLog);
        } catch (Exception e) {
            log.error("logging activity error: ", e);
        }
    }

    /**
     * Logs the deletion of a book by recording an activity log.
     *
     * @param id the ID of the deleted book
     * @throws InvalidException if the current user's access token is invalid
     */
    @Override
    public void logDeleteBook(long id) {
        try {
            List<ActivityDescription> descriptions = new ArrayList<>();
            descriptions.add(ActivityDescription.from("bookId", String.valueOf(id) + " → " + "none" , "Book id"));
            String email = SecurityUtil.getCurrentUserLogin()
                            .orElseThrow(() -> new InvalidException("Access Token invalid"));
            User user = userService.handleGetUserByUsername(email);
            ActivityLog activityLog = ActivityLog.formatLogMessage(ActivityGroup.BOOK, ActivityType.DELETE_BOOK, descriptions);
            activityLogService.log(user, activityLog);
        } catch (Exception e) {
            log.error("logging activity error: ", e);
        }
    }
}
