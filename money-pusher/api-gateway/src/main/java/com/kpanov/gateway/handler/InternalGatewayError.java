package com.kpanov.gateway.handler;

public class InternalGatewayError extends RuntimeException {
  public InternalGatewayError(String message) {
    super(message);
  }
}
