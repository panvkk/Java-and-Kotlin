package com.kpanov.common.event.transfer;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public record TransferDeductionFailedEvent(
        @JsonUnwrapped TransferMetadata metadata
) { }
