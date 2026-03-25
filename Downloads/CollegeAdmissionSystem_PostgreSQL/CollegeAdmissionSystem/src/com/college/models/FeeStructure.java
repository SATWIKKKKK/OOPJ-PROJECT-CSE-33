package com.college.models;

public class FeeStructure {

    private String courseId;
    private String courseName;
    private double admissionFee;          // one-time
    private double tuitionFeePerSemester;
    private double libraryFeePerSemester;
    private double labFeePerSemester;
    private double examFeePerSemester;
    private double hostelFeePerYear;
    private double transportFeePerYear;

    public FeeStructure(String courseId, String courseName, double admissionFee,
                        double tuitionFeePerSemester, double libraryFeePerSemester,
                        double labFeePerSemester, double examFeePerSemester,
                        double hostelFeePerYear, double transportFeePerYear) {
        this.courseId              = courseId;
        this.courseName            = courseName;
        this.admissionFee          = admissionFee;
        this.tuitionFeePerSemester = tuitionFeePerSemester;
        this.libraryFeePerSemester = libraryFeePerSemester;
        this.labFeePerSemester     = labFeePerSemester;
        this.examFeePerSemester    = examFeePerSemester;
        this.hostelFeePerYear      = hostelFeePerYear;
        this.transportFeePerYear   = transportFeePerYear;
    }

    // Getters
    public String getCourseId()              { return courseId; }
    public String getCourseName()            { return courseName; }
    public double getAdmissionFee()          { return admissionFee; }
    public double getTuitionFeePerSemester() { return tuitionFeePerSemester; }
    public double getLibraryFeePerSemester() { return libraryFeePerSemester; }
    public double getLabFeePerSemester()     { return labFeePerSemester; }
    public double getExamFeePerSemester()    { return examFeePerSemester; }
    public double getHostelFeePerYear()      { return hostelFeePerYear; }
    public double getTransportFeePerYear()   { return transportFeePerYear; }

    // Calculate total semester fee
    public double getTotalSemesterFee(boolean includeHostel, boolean includeTransport) {
        double total = tuitionFeePerSemester + libraryFeePerSemester
                     + labFeePerSemester + examFeePerSemester;
        if (includeHostel)    total += hostelFeePerYear / 2.0;
        if (includeTransport) total += transportFeePerYear / 2.0;
        return total;
    }

    public void displayFeeStructure() {
        System.out.println("\n  ╔═══════════════════════════════════════════════════════╗");
        System.out.println("  ║                    FEE STRUCTURE                      ║");
        System.out.println("  ╠═══════════════════════════════════════════════════════╣");
        System.out.printf("  ║  Course          : %-36s ║%n", courseName);
        System.out.println("  ╠═══════════════════════════════════════════════════════╣");
        System.out.printf("  ║  Admission Fee   : Rs. %-33.2f ║%n", admissionFee);
        System.out.printf("  ║  Tuition/Sem     : Rs. %-33.2f ║%n", tuitionFeePerSemester);
        System.out.printf("  ║  Library/Sem     : Rs. %-33.2f ║%n", libraryFeePerSemester);
        System.out.printf("  ║  Lab/Sem         : Rs. %-33.2f ║%n", labFeePerSemester);
        System.out.printf("  ║  Exam/Sem        : Rs. %-33.2f ║%n", examFeePerSemester);
        System.out.printf("  ║  Hostel/Year     : Rs. %-33.2f ║%n", hostelFeePerYear);
        System.out.printf("  ║  Transport/Year  : Rs. %-33.2f ║%n", transportFeePerYear);
        System.out.println("  ╠═══════════════════════════════════════════════════════╣");
        System.out.printf("  ║  Total/Sem (excl. Hostel & Transport)                 ║%n");
        System.out.printf("  ║                  : Rs. %-33.2f ║%n", getTotalSemesterFee(false, false));
        System.out.printf("  ║  Total/Sem (incl. Hostel & Transport)                 ║%n");
        System.out.printf("  ║                  : Rs. %-33.2f ║%n", getTotalSemesterFee(true, true));
        System.out.println("  ╚═══════════════════════════════════════════════════════╝");
    }
}
