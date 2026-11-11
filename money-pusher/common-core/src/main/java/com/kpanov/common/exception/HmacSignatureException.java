package com.kpanov.common.exception;

public class HmacSignatureException extends RuntimeException {
    public HmacSignatureException(String message) {
        super(message);
    }
}
