package com.kpanov.transfer.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kpanov.common.util.Constants;
import jakarta.validation.constraints.*;

public record TransferRequestDto(
        @JsonProperty("target_user_email")
        @NotBlank @Email String targetUserEmail,

        @JsonProperty("transfer_amount")
        @NotNull @Positive Long transferAmount,

        @JsonProperty("additional_message")
        @Size(max = Constants.MAX_TRANSFER_ADDITIONAL_MESSAGE_SIZE)
        String additionalMessage
) { }