package com.college.interfaces;

// Interface for fee/payment operations
public interface Payable {
    double  calculateTotalFee(boolean includeHostel, boolean includeTransport);
    boolean processPayment(double amount, String paymentMethod, String paymentType);
}
