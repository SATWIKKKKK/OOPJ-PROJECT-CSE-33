package com.college;

import com.college.database.DBConnection;
import com.college.exceptions.AdmissionException;
import com.college.exceptions.PaymentException;
import com.college.models.*;
import com.college.services.*;
import java.util.*;


public class Main {

    static Scanner sc = new Scanner(System.in);
    static AdmissionService admissionService;
    static FeeService        feeService;
    static ReportService     reportService;

    static final String ADMIN_USER = "admin";
    static final String ADMIN_PASS = "admin123";

    public static void main(String[] args) {
        printBanner();

        // Connect to PostgreSQL
        if (!DBConnection.testConnection()) {
            System.out.println("\n  [✗] Cannot start application without a database connection.");
            System.out.println("  Please check your PostgreSQL config in DBConnection.java");
            System.out.println("  and ensure you have placed the JDBC driver in lib/");
            return;
        }

        admissionService = new AdmissionService();
        feeService       = new FeeService();
        reportService    = new ReportService(admissionService, feeService);

        // Graceful shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(DBConnection::close));

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Enter choice: ");
            switch (choice) {
                case 1 -> adminLogin();
                case 2 -> studentPortal();
                case 3 -> { System.out.println("\n  Thank you. Goodbye!"); running = false; }
                default -> System.out.println("  [!] Invalid choice.");
            }
        }
        sc.close();
    }

    // ── Banners ───────────────────────────────────────────────────────────────

    static void printBanner() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════════════════════╗");
        System.out.println("  ║       COLLEGE ADMISSION AND FEE MANAGEMENT SYSTEM        ║");
        System.out.println("  ║          Backed by PostgreSQL  |  Java OOP               ║");
        System.out.println("  ╚══════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    static void printMainMenu() {
        System.out.println("\n  ┌─────────────────────────────────────────┐");
        System.out.println("  │               MAIN MENU                  │");
        System.out.println("  ├─────────────────────────────────────────┤");
        System.out.println("  │  1. Admin Login                          │");
        System.out.println("  │  2. Student / Applicant Portal           │");
        System.out.println("  │  3. Exit                                 │");
        System.out.println("  └─────────────────────────────────────────┘");
    }

    // ── Admin ─────────────────────────────────────────────────────────────────

    static void adminLogin() {
        System.out.println("\n  ── Admin Login ──");
        System.out.print("  Username: "); String user = sc.nextLine().trim();
        System.out.print("  Password: "); String pass = sc.nextLine().trim();
        if (ADMIN_USER.equals(user) && ADMIN_PASS.equals(pass)) {
            System.out.println("  [✓] Welcome, Admin!"); adminMenu();
        } else {
            System.out.println("  [✗] Invalid credentials.");
        }
    }

    static void adminMenu() {
        boolean inAdmin = true;
        while (inAdmin) {
            System.out.println("\n  ┌──────────────────────────────────────────────┐");
            System.out.println("  │              ADMIN DASHBOARD                  │");
            System.out.println("  ├──────────────────────────────────────────────┤");
            System.out.println("  │  ADMISSIONS                                   │");
            System.out.println("  │   1. View All Courses                         │");
            System.out.println("  │   2. Add New Course                           │");
            System.out.println("  │   3. View All Applications                    │");
            System.out.println("  │   4. View Pending Applications                │");
            System.out.println("  │   5. Approve Application                      │");
            System.out.println("  │   6. Reject Application                       │");
            System.out.println("  │   7. View All Students                        │");
            System.out.println("  │   8. View Student Details                     │");
            System.out.println("  │  FEES                                         │");
            System.out.println("  │   9. View Fee Structures                      │");
            System.out.println("  │  10. View All Payments                        │");
            System.out.println("  │  11. View Payments by Student                 │");
            System.out.println("  │  REPORTS                                      │");
            System.out.println("  │  12. Admission Report                         │");
            System.out.println("  │  13. Fee Collection Report                    │");
            System.out.println("  │   0. Logout                                   │");
            System.out.println("  └──────────────────────────────────────────────┘");
            int ch = readInt("  Choose: ");
            switch (ch) {
                case 1  -> viewAllCourses();
                case 2  -> addNewCourse();
                case 3  -> viewAllApplications();
                case 4  -> viewPendingApplications();
                case 5  -> approveApplication();
                case 6  -> rejectApplication();
                case 7  -> viewAllStudents();
                case 8  -> viewStudentDetails();
                case 9  -> viewFeeStructures();
                case 10 -> viewAllPayments();
                case 11 -> viewPaymentsByStudent();
                case 12 -> reportService.generateAdmissionReport();
                case 13 -> reportService.generateFeeReport();
                case 0  -> { System.out.println("  Logged out."); inAdmin = false; }
                default -> System.out.println("  [!] Invalid option.");
            }
        }
    }

    static void viewAllCourses() {
        System.out.println("\n  ── Available Courses ──");
        List<Course> list = admissionService.getAllCourses();
        if (list.isEmpty()) { System.out.println("  No courses found."); return; }
        list.forEach(System.out::println);
    }

    static void addNewCourse() {
        System.out.println("\n  ── Add New Course ──");
        System.out.print("  Course ID          : "); String id   = sc.nextLine().trim().toUpperCase();
        System.out.print("  Course Name        : "); String name = sc.nextLine().trim();
        System.out.print("  Type (UG/PG/DIPLOMA): "); String type = sc.nextLine().trim().toUpperCase();
        int    dur    = readInt(   "  Duration (years)  : ");
        int    seats  = readInt(   "  Total Seats       : ");
        double minPct = readDouble("  Min Score (%%)     : ");
        System.out.print("  Department         : "); String dept = sc.nextLine().trim();
        System.out.print("  Description        : "); String desc = sc.nextLine().trim();

        if (admissionService.getCourseById(id) != null) {
            System.out.println("  [!] Course ID already exists."); return;
        }
        Course c = new Course(id, name, type, dur, seats, minPct, dept, desc);
        admissionService.addCourse(c);

        double af = readDouble("  Admission Fee (Rs)  : ");
        double tf = readDouble("  Tuition/Sem  (Rs)   : ");
        feeService.addFeeStructure(new FeeStructure(id, name, af, tf, 2000, 3000, 2000, 50000, 12000));
        System.out.println("  [✓] Course and fee structure added: " + id);
    }

    static void viewAllApplications() {
        System.out.println("\n  ── All Applications ──");
        List<Applicant> list = admissionService.getAll();
        if (list.isEmpty()) { System.out.println("  No applications found."); return; }
        printSep();
        System.out.printf("  %-10s | %-20s | %-30s | %s%n", "App ID","Name","Course","Status");
        printSep();
        list.forEach(a -> System.out.printf("  %-10s | %-20s | %-30s | %s%n",
                a.getId(), a.getName(), a.getAppliedCourseName(), a.getApplicationStatus()));
        printSep();
        System.out.println("  Total: " + list.size());
    }

    static void viewPendingApplications() {
        System.out.println("\n  ── Pending Applications ──");
        List<Applicant> list = admissionService.getPendingApplications();
        if (list.isEmpty()) { System.out.println("  No pending applications."); return; }
        printSep();
        list.forEach(a -> System.out.printf("  %-10s | %-20s | %-30s | Score: %.1f%%%n",
                a.getId(), a.getName(), a.getAppliedCourseName(), a.getPreviousPercentage()));
        printSep();
    }

    static void approveApplication() {
        System.out.print("\n  Application ID to approve: ");
        String id = sc.nextLine().trim();
        try {
            Student s = admissionService.approveApplication(id);
            System.out.println("  [✓] Approved! Student enrolled.");
            s.displayInfo();
        } catch (AdmissionException e) {
            System.out.println("  [Error] " + e.getMessage());
        }
    }

    static void rejectApplication() {
        System.out.print("\n  Application ID to reject: ");
        String id = sc.nextLine().trim();
        System.out.print("  Reason: ");
        String reason = sc.nextLine().trim();
        try {
            admissionService.rejectApplication(id, reason);
            System.out.println("  [✓] Application rejected.");
        } catch (AdmissionException e) {
            System.out.println("  [Error] " + e.getMessage());
        }
    }

    static void viewAllStudents() {
        System.out.println("\n  ── All Enrolled Students ──");
        List<Student> list = admissionService.getAllStudents();
        if (list.isEmpty()) { System.out.println("  No students enrolled yet."); return; }
        printSep();
        System.out.printf("  %-10s | %-20s | %-30s | Sem | Status%n","StudentID","Name","Course");
        printSep();
        list.forEach(s -> System.out.printf("  %-10s | %-20s | %-30s |  %d  | %s%n",
                s.getStudentId(), s.getName(), s.getEnrolledCourse(), s.getSemester(), s.getStatus()));
        printSep();
        System.out.println("  Total: " + list.size());
    }

    static void viewStudentDetails() {
        System.out.print("\n  Student ID: ");
        String id = sc.nextLine().trim();
        Student s = admissionService.getStudentById(id);
        if (s == null) { System.out.println("  [!] Not found."); return; }
        s.displayInfo();
        reportService.generateStudentFeeReport(id);
    }

    static void viewFeeStructures() {
        System.out.println("\n  ── Fee Structures ──");
        List<FeeStructure> list = feeService.getAllFeeStructures();
        if (list.isEmpty()) { System.out.println("  No fee structures."); return; }
        list.forEach(FeeStructure::displayFeeStructure);
    }

    static void viewAllPayments() {
        System.out.println("\n  ── All Payment Records ──");
        List<Payment> list = feeService.getAll();
        if (list.isEmpty()) { System.out.println("  No payments."); return; }
        printSep();
        System.out.printf("  %-10s | %-10s | %-12s | %-9s | %-12s | %s%n",
                "PayID","StudentID","Type","Method","Amount","Date");
        printSep();
        list.forEach(System.out::println);
        printSep();
        System.out.printf("  Total collected: Rs. %.2f%n", feeService.getTotalCollected());
    }

    static void viewPaymentsByStudent() {
        System.out.print("\n  Student ID: ");
        reportService.generateStudentFeeReport(sc.nextLine().trim());
    }

    // ── Student Portal ────────────────────────────────────────────────────────

    static void studentPortal() {
        boolean inPortal = true;
        while (inPortal) {
            System.out.println("\n  ┌──────────────────────────────────────────────┐");
            System.out.println("  │           STUDENT / APPLICANT PORTAL          │");
            System.out.println("  ├──────────────────────────────────────────────┤");
            System.out.println("  │   1. Apply for Admission                      │");
            System.out.println("  │   2. Check Application Status                 │");
            System.out.println("  │   3. View All Available Courses               │");
            System.out.println("  │   4. View Fee Structure for a Course          │");
            System.out.println("  │   5. Pay Fees                                 │");
            System.out.println("  │   6. View My Payment History                  │");
            System.out.println("  │   7. View My Student Profile                  │");
            System.out.println("  │   0. Back                                     │");
            System.out.println("  └──────────────────────────────────────────────┘");
            int ch = readInt("  Choose: ");
            switch (ch) {
                case 1 -> applyForAdmission();
                case 2 -> checkApplicationStatus();
                case 3 -> viewAllCourses();
                case 4 -> viewFeeForCourse();
                case 5 -> payFees();
                case 6 -> viewMyPayments();
                case 7 -> viewMyProfile();
                case 0 -> inPortal = false;
                default -> System.out.println("  [!] Invalid option.");
            }
        }
    }

    static void applyForAdmission() {
        System.out.println("\n  ── New Admission Application ──");
        System.out.println("  Available Courses:");
        admissionService.getAllCourses().forEach(System.out::println);
        System.out.println();
        System.out.print("  Full Name           : "); String name   = sc.nextLine().trim();
        System.out.print("  Email               : "); String email  = sc.nextLine().trim();
        System.out.print("  Phone               : "); String phone  = sc.nextLine().trim();
        System.out.print("  Address             : "); String addr   = sc.nextLine().trim();
        System.out.print("  Gender (M/F/Other)  : "); String gender = sc.nextLine().trim();
        System.out.print("  Date of Birth       : "); String dob    = sc.nextLine().trim();
        System.out.print("  Course ID to Apply  : "); String cid    = sc.nextLine().trim();
        System.out.print("  Previous Institution: "); String prev   = sc.nextLine().trim();
        double pct = readDouble("  Previous Score (%%) : ");
        try {
            Applicant app = admissionService.applyForAdmission(name, email, phone, addr, gender, dob, cid, prev, pct);
            System.out.println("\n  [✓] Application submitted successfully!");
            app.displayInfo();
            System.out.println("  *** Save your Application ID: " + app.getId() + " ***");
        } catch (AdmissionException e) {
            System.out.println("\n  [✗] " + e.getMessage());
        }
    }

    static void checkApplicationStatus() {
        System.out.print("\n  Application ID: ");
        String id = sc.nextLine().trim();
        Applicant app = admissionService.getById(id);
        if (app == null) { System.out.println("  [!] Not found."); return; }
        app.displayInfo();
    }

    static void viewFeeForCourse() {
        System.out.print("\n  Course ID: ");
        String id = sc.nextLine().trim().toUpperCase();
        FeeStructure fs = feeService.getFeeStructureByCourseId(id);
        if (fs == null) { System.out.println("  [!] Fee structure not found for: " + id); return; }
        fs.displayFeeStructure();
    }

    static void payFees() {
        System.out.println("\n  ── Fee Payment ──");
        System.out.print("  Student ID: ");
        String studentId = sc.nextLine().trim();
        Student student  = admissionService.getStudentById(studentId);
        if (student == null) { System.out.println("  [!] Student not found."); return; }
        student.displayInfo();

        FeeStructure fs = feeService.getFeeStructureByCourseId(student.getCourseId());
        if (fs == null) { System.out.println("  [!] Fee structure not found. Contact admin."); return; }
        fs.displayFeeStructure();

        System.out.println("\n  Payment Type:  1.TUITION  2.HOSTEL  3.TRANSPORT  4.EXAM  5.ADMISSION");
        int tc = readInt("  Select type: ");
        String[] types = {"TUITION","HOSTEL","TRANSPORT","EXAM","ADMISSION"};
        if (tc < 1 || tc > 5) { System.out.println("  Invalid."); return; }

        System.out.println("  Payment Method: 1.CASH  2.ONLINE  3.CHEQUE  4.DD");
        int mc = readInt("  Select method: ");
        String[] methods = {"CASH","ONLINE","CHEQUE","DD"};
        if (mc < 1 || mc > 4) { System.out.println("  Invalid."); return; }

        double amount = readDouble("  Amount (Rs.): ");
        System.out.print("  Semester (e.g. SEM-1): ");
        String semester = sc.nextLine().trim();
        if (semester.isBlank()) semester = "SEM-" + student.getSemester();

        try {
            feeService.setPaymentContext(studentId, student.getName(), student.getCourseId(), semester);
            Payment payment = feeService.pay(
                    studentId, student.getName(), student.getCourseId(),
                    amount, types[tc-1], methods[mc-1], semester);
            System.out.println("\n  [✓] Payment Successful!");
            payment.displayReceipt();
        } catch (PaymentException e) {
            System.out.println("  [✗] Payment Failed: " + e.getMessage());
        }
    }

    static void viewMyPayments() {
        System.out.print("\n  Student ID: ");
        reportService.generateStudentFeeReport(sc.nextLine().trim());
    }

    static void viewMyProfile() {
        System.out.print("\n  Student ID: ");
        String id = sc.nextLine().trim();
        Student s = admissionService.getStudentById(id);
        if (s == null) { System.out.println("  [!] Not found."); return; }
        s.displayInfo();
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try   { return Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("  [!] Enter a valid number."); }
        }
    }

    static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try   { return Double.parseDouble(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("  [!] Enter a valid number."); }
        }
    }

    static void printSep() {
        System.out.println("  " + "─".repeat(80));
    }
}
