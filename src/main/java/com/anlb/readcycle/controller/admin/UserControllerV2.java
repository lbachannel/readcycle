package com.anlb.readcycle.controller.admin;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anlb.readcycle.dto.response.ResultPaginateDto;
import com.anlb.readcycle.service.IUserService;
import com.anlb.readcycle.service.criteria.UserCriteria;
import com.anlb.readcycle.utils.anotation.ApiMessage;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class UserControllerV2 {

    private final IUserService userService;
    
    @GetMapping("/users")
    @ApiMessage("Get all users")
    public ResponseEntity<ResultPaginateDto> getAllUsers(@ParameterObject UserCriteria criteria, @ParameterObject Pageable pageable) {
        return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(userService.handleGetAllUsers(criteria, pageable));
    }
}
