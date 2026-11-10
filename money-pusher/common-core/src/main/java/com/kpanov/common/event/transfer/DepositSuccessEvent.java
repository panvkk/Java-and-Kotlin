package com.kpanov.common.event.transfer;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public record DepositSuccessEvent(
        @JsonUnwrapped TransferMetadata metadata
) { }
