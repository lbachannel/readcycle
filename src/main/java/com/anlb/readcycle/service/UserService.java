package com.anlb.readcycle.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.anlb.readcycle.domain.User;
import com.anlb.readcycle.domain.dto.RegisterDTO;
import com.anlb.readcycle.repository.UserRepository;
import com.anlb.readcycle.utils.exception.RegisterValidator;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // mapper DTO -> User
    public User registerDTOtoUser(RegisterDTO registerDTO) {
        User user = new User();
        user.setName(registerDTO.getFirstName() + " " + registerDTO.getLastName());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(registerDTO.getPassword());
        if (RegisterValidator.isValidDateFormat(registerDTO.getDateOfBirth())) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            user.setDateOfBirth(LocalDate.parse(registerDTO.getDateOfBirth(), formatter));
        }

        return user;
    }

    public User handleCreateUser(User user) {
        return this.userRepository.save(user);
    }

    public boolean handleCheckExistsByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }
}
