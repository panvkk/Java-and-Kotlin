package com.kpanov.transfer.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

public record TransferRequestDto(
        @JsonProperty("target_user_email")
        @NotBlank @Email String targetUserEmail,

        @JsonProperty("transfer_amount")
        @NotNull @Positive Long transferAmount,

        @JsonProperty("additional_message")
        @Size(max = 255) String additionalMessage
) { }