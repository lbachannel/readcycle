package com.anlb.readcycle.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.anlb.readcycle.domain.Book;
import com.anlb.readcycle.domain.Role;
import com.anlb.readcycle.dto.response.AdminDashboardResponseDto;
import com.anlb.readcycle.dto.response.BookStatsDto;
import com.anlb.readcycle.repository.BookRepository;
import com.anlb.readcycle.repository.BorrowRepository;
import com.anlb.readcycle.repository.RoleRepository;
import com.anlb.readcycle.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IDashboardService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final RoleRepository roleRepository;
    private final BorrowRepository borrowRepository;

    public Map<String, Long> getDashboardStats() {
        Role adminRole = roleRepository.findByName("admin");
        Role userRole = roleRepository.findByName("user");
        Map<String, Long> response = new HashMap<>();
        response.put("countUser", userRepository.countByRole(userRole));
        response.put("countAdmin", userRepository.countByRole(adminRole));
        response.put("countBook", (Long) bookRepository.count());
        return response;
    }

    public AdminDashboardResponseDto getDashboardStatsBooks() {
        List<Book> books = bookRepository.findAll();
        List<BookStatsDto> bookStatsList = new ArrayList<>();
        
        for (Book book : books) {
            String category = book.getCategory();
            String title = book.getTitle();

            long borrowQty = borrowRepository.countByBook(book);

            long currentQty = book.getQuantity();

            long totalQty = borrowQty == 0 ? currentQty : (currentQty + borrowQty);
    
            BookStatsDto bookStatsDto = new BookStatsDto(category, title, totalQty, currentQty, borrowQty);
            bookStatsList.add(bookStatsDto);
        }

        return new AdminDashboardResponseDto(bookStatsList);
    }
}
