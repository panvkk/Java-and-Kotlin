package com.kpanov.clicker.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/clicker")
public class ClickerController {

    public ResponseEntity<Void> increaseUserBalance() {


        return ResponseEntity.ok().build();
    }
}
