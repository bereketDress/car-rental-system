package com.crms.exception;

public class OutstandingBalanceException extends RuntimeException {

    public OutstandingBalanceException(String message) {
        super(message);
    }
}