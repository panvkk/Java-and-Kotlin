package com.kpanov.clicker.util;

public class Messages {
    public static final String BALANCE_NOT_FOUND = "Balance by userEmail = %s not found.";
    public static final String PROCESSED_SAGA_PHASE = "Phase %s of transfer %d already processed, dropping duplicate event.";
    public static final String DEPOSIT_WITHOUT_DEDUCT = "Deposit phase can not running without processed deduct phase, dropping event.";
}
