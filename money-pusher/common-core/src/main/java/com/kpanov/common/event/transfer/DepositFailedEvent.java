package com.kpanov.common.event.transfer;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public record DepositFailedEvent(
        @JsonUnwrapped TransferMetadata metadata
) { }
