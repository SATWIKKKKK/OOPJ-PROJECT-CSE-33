package com.college.models;

// ENCAPSULATION: All fields private, accessed via getters/setters
public class Course {

    private String courseId;
    private String courseName;
    private String courseType;   // UG | PG | DIPLOMA
    private int    duration;     // in years
    private int    totalSeats;
    private int    availableSeats;
    private double minPercentageRequired;
    private String department;
    private String description;

    public Course(String courseId, String courseName, String courseType,
                  int duration, int totalSeats, double minPercentageRequired,
                  String department, String description) {
        this.courseId              = courseId;
        this.courseName            = courseName;
        this.courseType            = courseType;
        this.duration              = duration;
        this.totalSeats            = totalSeats;
        this.availableSeats        = totalSeats;
        this.minPercentageRequired = minPercentageRequired;
        this.department            = department;
        this.description           = description;
    }

    // Getters
    public String getCourseId()              { return courseId; }
    public String getCourseName()            { return courseName; }
    public String getCourseType()            { return courseType; }
    public int    getDuration()              { return duration; }
    public int    getTotalSeats()            { return totalSeats; }
    public int    getAvailableSeats()        { return availableSeats; }
    public double getMinPercentageRequired() { return minPercentageRequired; }
    public String getDepartment()            { return department; }
    public String getDescription()           { return description; }

    // Setters
    public void setAvailableSeats(int s) { this.availableSeats = s; }
    public boolean hasAvailableSeats()   { return availableSeats > 0; }
    public void decrementSeats()         { if (availableSeats > 0) availableSeats--; }
    public void incrementSeats()         { availableSeats++; }

    public void displayInfo() {
        System.out.println("\n  ╔══════════════════════════════════════════════════╗");
        System.out.println("  ║               COURSE INFORMATION                 ║");
        System.out.println("  ╠══════════════════════════════════════════════════╣");
        System.out.printf("  ║  Course ID      : %-30s ║%n", courseId);
        System.out.printf("  ║  Course Name    : %-30s ║%n", courseName);
        System.out.printf("  ║  Type           : %-30s ║%n", courseType);
        System.out.printf("  ║  Department     : %-30s ║%n", department);
        System.out.printf("  ║  Duration       : %-27d yrs ║%n", duration);
        System.out.printf("  ║  Total Seats    : %-30d ║%n", totalSeats);
        System.out.printf("  ║  Avail. Seats   : %-30d ║%n", availableSeats);
        System.out.printf("  ║  Min Score (%%)  : %-30.1f ║%n", minPercentageRequired);
        System.out.printf("  ║  Description    : %-30s ║%n",
                description.length() > 30 ? description.substring(0, 27) + "..." : description);
        System.out.println("  ╚══════════════════════════════════════════════════╝");
    }

    @Override
    public String toString() {
        return String.format("  [%-7s] %-35s | %-8s | Dept: %-20s | Seats: %2d/%-2d | Min: %.1f%%",
                courseId, courseName, courseType, department,
                availableSeats, totalSeats, minPercentageRequired);
    }
}
