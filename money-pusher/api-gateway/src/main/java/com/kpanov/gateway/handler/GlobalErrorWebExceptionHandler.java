package com.kpanov.gateway.handler;

import com.kpanov.common.exception.InvalidJwtException;
import com.kpanov.common.exception.TokenExpiredException;
import com.kpanov.gateway.util.Messages;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.webflux.autoconfigure.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.webflux.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
@Order(-1)
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {
    private final Clock clock;

    public GlobalErrorWebExceptionHandler(
            ErrorAttributes errorAttributes,
            WebProperties webProperties,
            ApplicationContext applicationContext,
            ServerCodecConfigurer codecConfigurer,
            Clock defaultClock
    ) {
        super(errorAttributes, webProperties.getResources(), applicationContext);

        super.setMessageReaders(codecConfigurer.getReaders());
        super.setMessageWriters(codecConfigurer.getWriters());
        clock = defaultClock;
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderServerResponse);
    }

    public Mono<ServerResponse> renderServerResponse(ServerRequest request) {
        var errorNullable = super.getError(request);
        var error = errorNullable != null ? errorNullable : new InternalGatewayError(Messages.INTERNAL_GATEWAY_ERROR);

        var status = HttpStatus.INTERNAL_SERVER_ERROR;
        var message = Messages.INTERNAL_GATEWAY_ERROR;

        if(error instanceof InvalidJwtException || error instanceof TokenExpiredException) {
            status = HttpStatus.UNAUTHORIZED;
            message = error.getMessage();
        } else if(error instanceof WebClientResponseException) {
            status = (HttpStatus) ((WebClientResponseException) error).getStatusCode();
        } else if(error instanceof ResponseStatusException) {
            status = (HttpStatus) ((ResponseStatusException) error).getStatusCode();
        }

        // TODO Добавить traceID

        var response = new ErrorResponseDto(LocalDateTime.now(clock), error.toString(), message);

        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(response);
    }
}
