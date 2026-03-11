package com.college.exceptions;

// Custom Exception for Payment-related errors
public class PaymentException extends Exception {

    private final String errorCode;
    private final double amount;

    public PaymentException(String message, String errorCode, double amount) {
        super(message);
        this.errorCode = errorCode;
        this.amount    = amount;
    }

    public PaymentException(String message) {
        super(message);
        this.errorCode = "PAY_ERR";
        this.amount    = 0;
    }

    public String getErrorCode() { return errorCode; }
    public double getAmount()    { return amount; }

    @Override
    public String toString() {
        return "[PaymentException - " + errorCode + "] " + getMessage();
    }
}
