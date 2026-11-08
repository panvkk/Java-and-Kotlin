package com.kpanov.transfer.controller.dto;

import java.util.List;

public record UserTransfersResponseDto(
        List<TransferResponseDto> transfers
) { }
