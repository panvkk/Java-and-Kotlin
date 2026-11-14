package com.kpanov.common.event.transfer;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public record DepositRequestEvent(
    @JsonUnwrapped TransferMetadata metadata
) { }
