package com.kpanov.transfer.controller;

import com.kpanov.transfer.controller.dto.TransferRequestDto;
import com.kpanov.transfer.controller.dto.TransferResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/transfer")
public class TransferController {

    @PostMapping
    public ResponseEntity<TransferResponseDto> transfer(TransferRequestDto dto) {
        
    }
}
