package com.kpanov.transfer.util;

public class Messages {
    public static final String EQUAL_EMAILS = "User cannot transfer to himself.";
    public static final String USER_BY_EMAIL_NOT_EXISTS = "User by initiatorEmail = %s don't exists.";

    public static final String INVALID_SAGA_PHASE = "Transfer %d already processed or in invalid state, dropping duplicate event.";
}
