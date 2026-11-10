package com.kpanov.transfer.controller;

import com.kpanov.transfer.controller.dto.TransferRequestDto;
import com.kpanov.transfer.controller.dto.TransferResponseDto;
import com.kpanov.transfer.controller.dto.UserTransfersResponseDto;
import com.kpanov.transfer.controller.mapper.ControllerMappers;
import com.kpanov.transfer.service.TransferService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.kpanov.common.util.Constants.USERNAME_HEADER;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/transfers")
public class    TransferController {
    private final TransferService transferService;
    private final ControllerMappers mappers;

    @GetMapping
    public ResponseEntity<UserTransfersResponseDto> getTransfers(
            @Parameter(hidden = true)
            @RequestHeader(USERNAME_HEADER) String userEmail
    ) {
        var entities = transferService.getUserTransfers(userEmail);
        var response = new UserTransfersResponseDto(
                entities.stream()
                        .map(mappers::toDto)
                        .toList()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<TransferResponseDto> transfer(
            @Parameter(hidden = true)
            @RequestHeader(USERNAME_HEADER) String initiatorEmail,
            @Valid @RequestBody TransferRequestDto dto
    ) {
        var response = transferService.requestTransfer(mappers.toModel(dto, initiatorEmail));

        return ResponseEntity.accepted().body(mappers.toDto(response));
    }
}
