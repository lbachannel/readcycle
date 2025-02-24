package com.anlb.readcycle.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anlb.readcycle.service.CartService;
import com.anlb.readcycle.utils.anotation.ApiMessage;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    
    @PostMapping("/remove-carts")
    @ApiMessage("Delete carts")
    public ResponseEntity<Void> handleDeleteCarts(@RequestBody List<Long> ids) {
        cartService.handleDeleteCarts(ids);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
