package com.college.exceptions;

// Custom Exception for Admission-related errors
public class AdmissionException extends Exception {

    private final String errorCode;

    public AdmissionException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public AdmissionException(String message) {
        super(message);
        this.errorCode = "ADM_ERR";
    }

    public String getErrorCode() { return errorCode; }

    @Override
    public String toString() {
        return "[AdmissionException - " + errorCode + "] " + getMessage();
    }
}
