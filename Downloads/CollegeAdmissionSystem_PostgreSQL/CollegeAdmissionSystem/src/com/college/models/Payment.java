package com.college.models;

public class Payment {

    private String paymentId;
    private String studentId;
    private String studentName;
    private String courseId;
    private double amount;
    private String paymentDate;
    private String paymentType;   // ADMISSION | TUITION | HOSTEL | TRANSPORT | EXAM
    private String paymentMethod; // CASH | ONLINE | CHEQUE | DD
    private String semester;
    private String transactionId;
    private String status;        // SUCCESS | PENDING | FAILED

    public Payment(String paymentId, String studentId, String studentName, String courseId,
                   double amount, String paymentDate, String paymentType,
                   String paymentMethod, String semester, String transactionId) {
        this.paymentId     = paymentId;
        this.studentId     = studentId;
        this.studentName   = studentName;
        this.courseId      = courseId;
        this.amount        = amount;
        this.paymentDate   = paymentDate;
        this.paymentType   = paymentType;
        this.paymentMethod = paymentMethod;
        this.semester      = semester;
        this.transactionId = transactionId;
        this.status        = "SUCCESS";
    }

    // Getters
    public String getPaymentId()     { return paymentId; }
    public String getStudentId()     { return studentId; }
    public String getStudentName()   { return studentName; }
    public String getCourseId()      { return courseId; }
    public double getAmount()        { return amount; }
    public String getPaymentDate()   { return paymentDate; }
    public String getPaymentType()   { return paymentType; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getSemester()      { return semester; }
    public String getTransactionId() { return transactionId; }
    public String getStatus()        { return status; }

    public void setStatus(String status) { this.status = status; }

    public void displayReceipt() {
        System.out.println("\n  ╔═══════════════════════════════════════════════════════╗");
        System.out.println("  ║                   PAYMENT RECEIPT                     ║");
        System.out.println("  ╠═══════════════════════════════════════════════════════╣");
        System.out.printf("  ║  Payment ID      : %-36s ║%n", paymentId);
        System.out.printf("  ║  Transaction ID  : %-36s ║%n", transactionId);
        System.out.printf("  ║  Student ID      : %-36s ║%n", studentId);
        System.out.printf("  ║  Student Name    : %-36s ║%n", studentName);
        System.out.printf("  ║  Semester        : %-36s ║%n", semester);
        System.out.printf("  ║  Payment Type    : %-36s ║%n", paymentType);
        System.out.printf("  ║  Payment Method  : %-36s ║%n", paymentMethod);
        System.out.printf("  ║  Date            : %-36s ║%n", paymentDate);
        System.out.printf("  ║  Amount Paid     : Rs. %-33.2f ║%n", amount);
        System.out.printf("  ║  Status          : %-36s ║%n", status);
        System.out.println("  ╚═══════════════════════════════════════════════════════╝");
    }

    @Override
    public String toString() {
        return String.format("  %-10s | %-12s | %-12s | %-12s | Rs.%-10.2f | %s | %s",
                paymentId, studentId, paymentType, paymentMethod, amount, paymentDate, status);
    }
}
