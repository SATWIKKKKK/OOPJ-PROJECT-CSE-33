package com.college.models;

// INHERITANCE: Applicant extends Person
public class Applicant extends Person {

    private String appliedCourseId;
    private String appliedCourseName;
    private String previousInstitution;
    private double previousPercentage;
    private String applicationDate;
    private String applicationStatus; // PENDING | APPROVED | REJECTED
    private String remarks;

    public Applicant(String id, String name, String email, String phone, String address,
                     String gender, String dob, String appliedCourseId, String appliedCourseName,
                     String previousInstitution, double previousPercentage, String applicationDate) {
        super(id, name, email, phone, address, gender, dob);
        this.appliedCourseId      = appliedCourseId;
        this.appliedCourseName    = appliedCourseName;
        this.previousInstitution  = previousInstitution;
        this.previousPercentage   = previousPercentage;
        this.applicationDate      = applicationDate;
        this.applicationStatus    = "PENDING";
        this.remarks              = "-";
    }

    // Getters
    public String getAppliedCourseId()     { return appliedCourseId; }
    public String getAppliedCourseName()   { return appliedCourseName; }
    public String getPreviousInstitution() { return previousInstitution; }
    public double getPreviousPercentage()  { return previousPercentage; }
    public String getApplicationDate()    { return applicationDate; }
    public String getApplicationStatus()  { return applicationStatus; }
    public String getRemarks()            { return remarks; }

    // Setters
    public void setApplicationStatus(String status) { this.applicationStatus = status; }
    public void setRemarks(String remarks)          { this.remarks = remarks; }

    // POLYMORPHISM: overriding abstract methods
    @Override
    public String getRole() { return "APPLICANT"; }

    @Override
    public void displayInfo() {
        System.out.println("\n  ╔══════════════════════════════════════════════════╗");
        System.out.println("  ║             APPLICANT INFORMATION                ║");
        System.out.println("  ╠══════════════════════════════════════════════════╣");
        System.out.printf("  ║  Application ID    : %-28s ║%n", getId());
        System.out.printf("  ║  Name              : %-28s ║%n", getName());
        System.out.printf("  ║  Email             : %-28s ║%n", getEmail());
        System.out.printf("  ║  Phone             : %-28s ║%n", getPhone());
        System.out.printf("  ║  Gender            : %-28s ║%n", getGender());
        System.out.printf("  ║  Date of Birth     : %-28s ║%n", getDateOfBirth());
        System.out.printf("  ║  Applied Course    : %-28s ║%n", appliedCourseName);
        System.out.printf("  ║  Prev. Institution : %-28s ║%n", previousInstitution);
        System.out.printf("  ║  Prev. Score (%%)   : %-28.2f ║%n", previousPercentage);
        System.out.printf("  ║  Application Date  : %-28s ║%n", applicationDate);
        System.out.printf("  ║  Status            : %-28s ║%n", applicationStatus);
        System.out.printf("  ║  Remarks           : %-28s ║%n", remarks);
        System.out.println("  ╚══════════════════════════════════════════════════╝");
    }
}
