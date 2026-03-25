package models;

import course.Course;
import java.util.ArrayList;
import java.util.List;

// Inherits from Person (Inheritance)
public class Student extends Person {

    public enum AdmissionStatus { PENDING, APPROVED, REJECTED, ENROLLED }

    private String studentRollNo;
    private String courseId;
    private String courseName;
    private int    admissionYear;
    private int    currentSemester;
    private double totalFeesPaid;
    private double totalFeesDue;
    private AdmissionStatus status;
    private String guardianName;
    private String guardianPhone;
    private double percentage10th;
    private double percentage12th;
    private List<String> paymentHistory;

    public Student(String id, String name, String email, String phone,
                   String address, int age, String courseId, String courseName,
                   int admissionYear, String guardianName, String guardianPhone,
                   double percentage10th, double percentage12th) {
        super(id, name, email, phone, address, age);
        this.studentRollNo   = id;
        this.courseId        = courseId;
        this.courseName      = courseName;
        this.admissionYear   = admissionYear;
        this.currentSemester = 1;
        this.totalFeesPaid   = 0.0;
        this.totalFeesDue    = 0.0;
        this.status          = AdmissionStatus.PENDING;
        this.guardianName    = guardianName;
        this.guardianPhone   = guardianPhone;
        this.percentage10th  = percentage10th;
        this.percentage12th  = percentage12th;
        this.paymentHistory  = new ArrayList<>();
    }

    @Override
    public String getRole() { return "Student"; }

    // Getters
    public String         getStudentRollNo()   { return studentRollNo; }
    public String         getCourseId()        { return courseId; }
    public String         getCourseName()      { return courseName; }
    public int            getAdmissionYear()   { return admissionYear; }
    public int            getCurrentSemester() { return currentSemester; }
    public double         getTotalFeesPaid()   { return totalFeesPaid; }
    public double         getTotalFeesDue()    { return totalFeesDue; }
    public AdmissionStatus getStatus()         { return status; }
    public String         getGuardianName()    { return guardianName; }
    public String         getGuardianPhone()   { return guardianPhone; }
    public double         getPercentage10th()  { return percentage10th; }
    public double         getPercentage12th()  { return percentage12th; }
    public List<String>   getPaymentHistory()  { return paymentHistory; }

    // Setters
    public void setStatus(AdmissionStatus status)       { this.status = status; }
    public void setCurrentSemester(int semester)        { this.currentSemester = semester; }
    public void setTotalFeesDue(double amount)          { this.totalFeesDue = amount; }
    public void addFeePayment(double amount, String ref) {
        this.totalFeesPaid += amount;
        this.totalFeesDue  = Math.max(0, this.totalFeesDue - amount);
        paymentHistory.add(ref);
    }

    public void displayDetails() {
        String border = "═".repeat(60);
        System.out.println("\n╔" + border + "╗");
        System.out.println("║          STUDENT PROFILE                                   ║");
        System.out.println("╠" + border + "╣");
        System.out.printf("║  Roll No       : %-41s║%n", studentRollNo);
        System.out.printf("║  Name          : %-41s║%n", getName());
        System.out.printf("║  Age           : %-41s║%n", getAge());
        System.out.printf("║  Email         : %-41s║%n", getEmail());
        System.out.printf("║  Phone         : %-41s║%n", getPhone());
        System.out.printf("║  Address       : %-41s║%n", getAddress());
        System.out.printf("║  Course        : %-41s║%n", courseName);
        System.out.printf("║  Semester      : %-41s║%n", currentSemester);
        System.out.printf("║  Adm. Year     : %-41s║%n", admissionYear);
        System.out.printf("║  Status        : %-41s║%n", status);
        System.out.printf("║  Guardian      : %-41s║%n", guardianName);
        System.out.printf("║  Guardian Ph   : %-41s║%n", guardianPhone);
        System.out.printf("║  10th %%        : %-41s║%n", percentage10th + "%");
        System.out.printf("║  12th %%        : %-41s║%n", percentage12th + "%");
        System.out.printf("║  Fees Paid     : Rs. %-38s║%n", String.format("%.2f", totalFeesPaid));
        System.out.printf("║  Fees Due      : Rs. %-38s║%n", String.format("%.2f", totalFeesDue));
        System.out.println("╚" + border + "╝");
    }
}
