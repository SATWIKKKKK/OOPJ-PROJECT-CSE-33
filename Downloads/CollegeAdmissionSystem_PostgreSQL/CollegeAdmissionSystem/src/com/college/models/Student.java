package com.college.models;

// INHERITANCE: Student extends Person
public class Student extends Person {

    private String studentId;
    private String enrolledCourse;
    private String courseId;
    private int    semester;
    private double cgpa;
    private String admissionYear;
    private String status; // ACTIVE | INACTIVE | GRADUATED

    public Student(String id, String name, String email, String phone, String address,
                   String gender, String dob, String studentId,
                   String enrolledCourse, String courseId, int semester, String admissionYear) {
        super(id, name, email, phone, address, gender, dob);
        this.studentId     = studentId;
        this.enrolledCourse = enrolledCourse;
        this.courseId      = courseId;
        this.semester      = semester;
        this.cgpa          = 0.0;
        this.admissionYear = admissionYear;
        this.status        = "ACTIVE";
    }

    // Getters
    public String getStudentId()      { return studentId; }
    public String getEnrolledCourse() { return enrolledCourse; }
    public String getCourseId()       { return courseId; }
    public int    getSemester()       { return semester; }
    public double getCgpa()           { return cgpa; }
    public String getAdmissionYear()  { return admissionYear; }
    public String getStatus()         { return status; }

    // Setters
    public void setSemester(int s)         { this.semester = s; }
    public void setCgpa(double c)          { this.cgpa = c; }
    public void setStatus(String status)   { this.status = status; }
    public void setEnrolledCourse(String c){ this.enrolledCourse = c; }

    // POLYMORPHISM: overriding abstract methods from Person
    @Override
    public String getRole() { return "STUDENT"; }

    @Override
    public void displayInfo() {
        System.out.println("\n  ╔══════════════════════════════════════════════════╗");
        System.out.println("  ║              STUDENT INFORMATION                 ║");
        System.out.println("  ╠══════════════════════════════════════════════════╣");
        System.out.printf("  ║  Name           : %-30s ║%n", getName());
        System.out.printf("  ║  Student ID     : %-30s ║%n", studentId);
        System.out.printf("  ║  Email          : %-30s ║%n", getEmail());
        System.out.printf("  ║  Phone          : %-30s ║%n", getPhone());
        System.out.printf("  ║  Gender         : %-30s ║%n", getGender());
        System.out.printf("  ║  Date of Birth  : %-30s ║%n", getDateOfBirth());
        System.out.printf("  ║  Course         : %-30s ║%n", enrolledCourse);
        System.out.printf("  ║  Course ID      : %-30s ║%n", courseId);
        System.out.printf("  ║  Semester       : %-30d ║%n", semester);
        System.out.printf("  ║  CGPA           : %-30.2f ║%n", cgpa);
        System.out.printf("  ║  Admission Year : %-30s ║%n", admissionYear);
        System.out.printf("  ║  Status         : %-30s ║%n", status);
        System.out.println("  ╚══════════════════════════════════════════════════╝");
    }
}
