package com.anlb.readcycle.mapper;

import org.springframework.stereotype.Service;

import com.anlb.readcycle.domain.Borrow;
import com.anlb.readcycle.dto.response.BorrowResponseDto;

@Service
public class BorrowMapper {

    public BorrowResponseDto convertBorrowToBorrowResponseDto(Borrow currentBorrow) {
        BorrowResponseDto response = new BorrowResponseDto();
        response.setId(currentBorrow.getId());
        response.setStatus(currentBorrow.getStatus());
        response.setBook(currentBorrow.getBook());
        response.setUser(currentBorrow.getUser());
        return response;
    }
    
}
