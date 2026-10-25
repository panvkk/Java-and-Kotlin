package com.kpanov.clicker.exception;

import java.util.NoSuchElementException;

public class BalanceNotFoundException extends NoSuchElementException {
    public BalanceNotFoundException(String message) {
        super(message);
    }
}
