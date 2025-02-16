package com.anlb.readcycle.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.anlb.readcycle.domain.Book;
import com.anlb.readcycle.domain.User;
import com.anlb.readcycle.dto.activitylog.ActivityDescription;
import com.anlb.readcycle.dto.activitylog.ActivityGroup;
import com.anlb.readcycle.dto.activitylog.ActivityLog;
import com.anlb.readcycle.dto.activitylog.ActivityType;
import com.anlb.readcycle.utils.SecurityUtil;
import com.anlb.readcycle.utils.exception.InvalidException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookLogService {

    private final ActivityLogService activityLogService;
    private final UserService userService;

    /**
     * Logs the creation of a new book, capturing its details as an activity log.
     *
     * @param book The {@link Book} object containing details of the created book.
     * @throws InvalidException If the current user's authentication token is invalid.
     */
    public void logCreateBook(Book book) throws InvalidException {
        String email = SecurityUtil.getCurrentUserLogin()
                            .orElseThrow(() -> new InvalidException("Access Token invalid"));
        User user = this.userService.handleGetUserByUsername(email);
        try {
            List<ActivityDescription> descriptions = new ArrayList<>();
            descriptions.add(ActivityDescription.from("bookId", String.valueOf(book.getId()), "Book id"));
            if (book.getCategory() != null) {
                descriptions.add(ActivityDescription.from("category", book.getCategory(), "Category"));
            }
            if (book.getTitle() != null) {
                descriptions.add(ActivityDescription.from("title", book.getTitle(), "Title"));
            }
            if (book.getAuthor() != null) {
                descriptions.add(ActivityDescription.from("author", book.getAuthor(), "Author"));
            }
            if (book.getPublisher() != null) {
                descriptions.add(ActivityDescription.from("publisher", book.getPublisher(), "Publisher"));
            }
            if (book.getThumb() != null) {
                descriptions.add(ActivityDescription.from("thumb", book.getThumb(), "Thumb"));
            }
            if (book.getQuantity() != 0) {
                descriptions.add(ActivityDescription.from("quantity", String.valueOf(book.getQuantity()), "Quantity"));
            }
            if (book.getStatus() != null) {
                descriptions.add(ActivityDescription.from("status", String.valueOf(book.getStatus()), "Status"));
            }
            descriptions.add(ActivityDescription.from("isActive", book.isActive() ? "True" : "False", "Active"));

            ActivityLog activityLog = ActivityLog.formatLogMessage(ActivityGroup.BOOK, ActivityType.CREATE_BOOK, descriptions);
            activityLogService.log(user, activityLog);
        } catch (Exception e) {
            log.error("logging activity error: ", e);
        }
    }


}
