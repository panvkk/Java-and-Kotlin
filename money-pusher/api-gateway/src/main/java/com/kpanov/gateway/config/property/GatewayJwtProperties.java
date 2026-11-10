package com.kpanov.gateway.config.property;

import com.kpanov.common.util.Constants;
import com.kpanov.common.util.Messages;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("application.security.jwt")
public record GatewayJwtProperties(
        @NotNull(message = Messages.JWT_SECRET_BLANK)
        @Size(min = Constants.MIN_JWT_SECRET_SIZE, message = Messages.JWT_SECRET_SHORT)
        String secretKey
) { }