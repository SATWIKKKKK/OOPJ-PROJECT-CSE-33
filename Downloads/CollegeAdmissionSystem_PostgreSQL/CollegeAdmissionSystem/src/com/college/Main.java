package com.college;

import com.college.database.DBConnection;
import com.college.exceptions.AdmissionException;
import com.college.exceptions.PaymentException;
import com.college.models.*;
import com.college.services.*;
import com.college.utils.EnvConfig;
import java.io.*;
import java.util.*;


public class Main {

    static Scanner sc = new Scanner(System.in);
    static AdmissionService admissionService;
    static FeeService        feeService;
    static ReportService     reportService;

    // Admin credentials loaded from .env (can be changed at runtime via Admin menu)
    static String ADMIN_USER = EnvConfig.get("ADMIN_USERNAME", "admin");
    static String ADMIN_PASS = EnvConfig.get("ADMIN_PASSWORD", "admin123");

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
            System.out.println("  │  ADVANCED                                     │");
            System.out.println("  │  14. Search Courses by Keyword                │");
            System.out.println("  │  15. Update Student (Semester/CGPA/Status)     │");
            System.out.println("  │  16. Delete a Course                          │");
            System.out.println("  │  17. Update Fee Structure                     │");
            System.out.println("  │  18. Scholarship Calculator                   │");
            System.out.println("  │  19. Export Data to CSV                        │");
            System.out.println("  │  20. Change Admin Password                    │");
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
                case 14 -> searchCourses();
                case 15 -> updateStudent();
                case 16 -> deleteCourse();
                case 17 -> updateFeeStructure();
                case 18 -> scholarshipCalculator();
                case 19 -> exportDataToCsv();
                case 20 -> changeAdminPassword();
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
            System.out.println("  │   8. Search Courses by Keyword                │");
            System.out.println("  │   9. Fee Calculator                           │");
            System.out.println("  │  10. Check Scholarship Eligibility            │");
            System.out.println("  │   0. Back                                     │");
            System.out.println("  └──────────────────────────────────────────────┘");
            int ch = readInt("  Choose: ");
            switch (ch) {
                case 1  -> applyForAdmission();
                case 2  -> checkApplicationStatus();
                case 3  -> viewAllCourses();
                case 4  -> viewFeeForCourse();
                case 5  -> payFees();
                case 6  -> viewMyPayments();
                case 7  -> viewMyProfile();
                case 8  -> searchCourses();
                case 9  -> feeCalculator();
                case 10 -> checkScholarshipEligibility();
                case 0  -> inPortal = false;
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

    // ══════════════════════════════════════════════════════════════════════════
    //  ADVANCED FEATURES
    // ══════════════════════════════════════════════════════════════════════════

    // ── 14. Search Courses by Keyword ─────────────────────────────────────────
    static void searchCourses() {
        System.out.print("\n  Search keyword (name/dept/type): ");
        String keyword = sc.nextLine().trim().toLowerCase();
        if (keyword.isEmpty()) { System.out.println("  [!] Empty search."); return; }

        List<Course> all = admissionService.getAllCourses();
        List<Course> matches = new ArrayList<>();
        for (Course c : all) {
            if (c.getCourseName().toLowerCase().contains(keyword)
             || c.getDepartment().toLowerCase().contains(keyword)
             || c.getCourseType().toLowerCase().contains(keyword)
             || c.getCourseId().toLowerCase().contains(keyword)) {
                matches.add(c);
            }
        }
        if (matches.isEmpty()) { System.out.println("  No courses match '" + keyword + "'."); return; }
        System.out.println("\n  ── Search Results (" + matches.size() + " found) ──");
        matches.forEach(System.out::println);
    }

    // ── 15. Update Student (Semester / CGPA / Status) ─────────────────────────
    static void updateStudent() {
        System.out.print("\n  Student ID to update: ");
        String id = sc.nextLine().trim();
        Student s = admissionService.getStudentById(id);
        if (s == null) { System.out.println("  [!] Student not found."); return; }
        s.displayInfo();

        System.out.println("\n  What to update?");
        System.out.println("    1. Semester");
        System.out.println("    2. CGPA");
        System.out.println("    3. Status (ACTIVE/INACTIVE/GRADUATED)");
        int ch = readInt("  Choose: ");
        switch (ch) {
            case 1 -> {
                int sem = readInt("  New Semester: ");
                admissionService.updateStudentField(id, "semester", String.valueOf(sem));
                System.out.println("  [✓] Semester updated to " + sem);
            }
            case 2 -> {
                double cgpa = readDouble("  New CGPA: ");
                admissionService.updateStudentField(id, "cgpa", String.valueOf(cgpa));
                System.out.println("  [✓] CGPA updated to " + cgpa);
            }
            case 3 -> {
                System.out.print("  New Status (ACTIVE/INACTIVE/GRADUATED): ");
                String status = sc.nextLine().trim().toUpperCase();
                if (!status.equals("ACTIVE") && !status.equals("INACTIVE") && !status.equals("GRADUATED")) {
                    System.out.println("  [!] Invalid status."); return;
                }
                admissionService.updateStudentField(id, "status", status);
                System.out.println("  [✓] Status updated to " + status);
            }
            default -> System.out.println("  [!] Invalid option.");
        }
    }

    // ── 16. Delete a Course ───────────────────────────────────────────────────
    static void deleteCourse() {
        System.out.println("\n  ── Delete Course ──");
        System.out.println("  WARNING: This will remove the course and its fee structure.");
        System.out.println("  (Cannot delete if students are enrolled or applications exist.)");
        System.out.print("  Course ID to delete: ");
        String id = sc.nextLine().trim().toUpperCase();

        Course c = admissionService.getCourseById(id);
        if (c == null) { System.out.println("  [!] Course not found."); return; }
        c.displayInfo();

        System.out.print("  Are you sure? (yes/no): ");
        String confirm = sc.nextLine().trim().toLowerCase();
        if (!"yes".equals(confirm)) { System.out.println("  Cancelled."); return; }

        boolean deleted = admissionService.deleteCourse(id);
        if (deleted) System.out.println("  [✓] Course " + id + " deleted.");
        else         System.out.println("  [✗] Could not delete. Students or applications may still reference this course.");
    }

    // ── 17. Update Fee Structure ──────────────────────────────────────────────
    static void updateFeeStructure() {
        System.out.print("\n  Course ID to update fees: ");
        String id = sc.nextLine().trim().toUpperCase();
        FeeStructure fs = feeService.getFeeStructureByCourseId(id);
        if (fs == null) { System.out.println("  [!] Fee structure not found."); return; }
        fs.displayFeeStructure();

        System.out.println("\n  Enter new values (press Enter to keep current):");
        double af = readDoubleOpt("  Admission Fee [" + fs.getAdmissionFee() + "]: ", fs.getAdmissionFee());
        double tf = readDoubleOpt("  Tuition/Sem   [" + fs.getTuitionFeePerSemester() + "]: ", fs.getTuitionFeePerSemester());
        double lf = readDoubleOpt("  Library/Sem   [" + fs.getLibraryFeePerSemester() + "]: ", fs.getLibraryFeePerSemester());
        double lb = readDoubleOpt("  Lab/Sem       [" + fs.getLabFeePerSemester() + "]: ", fs.getLabFeePerSemester());
        double ef = readDoubleOpt("  Exam/Sem      [" + fs.getExamFeePerSemester() + "]: ", fs.getExamFeePerSemester());
        double hf = readDoubleOpt("  Hostel/Year   [" + fs.getHostelFeePerYear() + "]: ", fs.getHostelFeePerYear());
        double tp = readDoubleOpt("  Transport/Year[" + fs.getTransportFeePerYear() + "]: ", fs.getTransportFeePerYear());

        feeService.updateFeeStructure(id, af, tf, lf, lb, ef, hf, tp);
        System.out.println("  [✓] Fee structure updated for " + id);
    }

    // ── 18. Scholarship Calculator ────────────────────────────────────────────
    static void scholarshipCalculator() {
        System.out.println("\n  ── Scholarship Calculator ──");
        System.out.println("  Scholarship slabs based on previous score:");
        System.out.println("    95%+ → 50%% tuition waiver");
        System.out.println("    90%+ → 30%% tuition waiver");
        System.out.println("    85%+ → 15%% tuition waiver");
        System.out.println("    80%+ → 10%% tuition waiver");
        System.out.println("    Below 80%% → No scholarship");
        System.out.println();

        System.out.print("  Student ID (or press Enter to calculate manually): ");
        String studentId = sc.nextLine().trim();

        double score;
        String courseId;

        if (!studentId.isEmpty()) {
            Student st = admissionService.getStudentById(studentId);
            if (st == null) { System.out.println("  [!] Student not found."); return; }
            // Find applicant score by searching approved applications
            List<Applicant> apps = admissionService.getByStatus("APPROVED");
            double foundScore = -1;
            for (Applicant a : apps) {
                if (a.getName().equalsIgnoreCase(st.getName())) {
                    foundScore = a.getPreviousPercentage();
                    break;
                }
            }
            if (foundScore < 0) {
                System.out.println("  [!] Could not find applicant record. Enter score manually.");
                score = readDouble("  Previous Score (%): ");
            } else {
                score = foundScore;
                System.out.printf("  Found score: %.2f%%%n", score);
            }
            courseId = st.getCourseId();
        } else {
            score    = readDouble("  Previous Score (%): ");
            System.out.print("  Course ID: ");
            courseId = sc.nextLine().trim().toUpperCase();
        }

        double waiver;
        if      (score >= 95) waiver = 0.50;
        else if (score >= 90) waiver = 0.30;
        else if (score >= 85) waiver = 0.15;
        else if (score >= 80) waiver = 0.10;
        else                  waiver = 0.00;

        FeeStructure fs = feeService.getFeeStructureByCourseId(courseId);
        if (fs == null) { System.out.println("  [!] Fee structure not found."); return; }

        double tuition  = fs.getTuitionFeePerSemester();
        double discount = tuition * waiver;
        double afterDiscount = tuition - discount;

        System.out.println("\n  ╔════════════════════════════════════════════════════╗");
        System.out.println("  ║            SCHOLARSHIP CALCULATION                ║");
        System.out.println("  ╠════════════════════════════════════════════════════╣");
        System.out.printf("  ║  Score             : %.2f%%                         ║%n", score);
        System.out.printf("  ║  Scholarship       : %.0f%% tuition waiver           ║%n", waiver * 100);
        System.out.printf("  ║  Tuition/Sem       : Rs. %-25.2f  ║%n", tuition);
        System.out.printf("  ║  Discount/Sem      : Rs. %-25.2f  ║%n", discount);
        System.out.printf("  ║  After Scholarship : Rs. %-25.2f  ║%n", afterDiscount);
        System.out.println("  ╚════════════════════════════════════════════════════╝");
    }

    // ── 19. Export Data to CSV ────────────────────────────────────────────────
    static void exportDataToCsv() {
        System.out.println("\n  ── Export Data to CSV ──");
        System.out.println("    1. Export Students");
        System.out.println("    2. Export Applications");
        System.out.println("    3. Export Payments");
        System.out.println("    4. Export Courses");
        int ch = readInt("  Choose: ");

        String filename;
        List<String> lines = new ArrayList<>();

        switch (ch) {
            case 1 -> {
                filename = "data/students_export.csv";
                lines.add("StudentID,Name,Email,Phone,Course,CourseID,Semester,CGPA,AdmissionYear,Status");
                for (Student s : admissionService.getAllStudents())
                    lines.add(String.format("%s,%s,%s,%s,%s,%s,%d,%.2f,%s,%s",
                        s.getStudentId(), s.getName(), s.getEmail(), s.getPhone(),
                        s.getEnrolledCourse(), s.getCourseId(), s.getSemester(),
                        s.getCgpa(), s.getAdmissionYear(), s.getStatus()));
            }
            case 2 -> {
                filename = "data/applications_export.csv";
                lines.add("AppID,Name,Email,Phone,Course,PrevInst,PrevScore,Date,Status,Remarks");
                for (Applicant a : admissionService.getAll())
                    lines.add(String.format("%s,%s,%s,%s,%s,%s,%.2f,%s,%s,%s",
                        a.getId(), a.getName(), a.getEmail(), a.getPhone(),
                        a.getAppliedCourseName(), a.getPreviousInstitution(),
                        a.getPreviousPercentage(), a.getApplicationDate(),
                        a.getApplicationStatus(), a.getRemarks()));
            }
            case 3 -> {
                filename = "data/payments_export.csv";
                lines.add("PayID,StudentID,StudentName,CourseID,Amount,Date,Type,Method,Semester,TxnID,Status");
                for (Payment p : feeService.getAll())
                    lines.add(String.format("%s,%s,%s,%s,%.2f,%s,%s,%s,%s,%s,%s",
                        p.getPaymentId(), p.getStudentId(), p.getStudentName(),
                        p.getCourseId(), p.getAmount(), p.getPaymentDate(),
                        p.getPaymentType(), p.getPaymentMethod(), p.getSemester(),
                        p.getTransactionId(), p.getStatus()));
            }
            case 4 -> {
                filename = "data/courses_export.csv";
                lines.add("CourseID,Name,Type,Duration,TotalSeats,AvailSeats,MinScore,Department,Description");
                for (Course c : admissionService.getAllCourses())
                    lines.add(String.format("%s,%s,%s,%d,%d,%d,%.2f,%s,%s",
                        c.getCourseId(), c.getCourseName(), c.getCourseType(),
                        c.getDuration(), c.getTotalSeats(), c.getAvailableSeats(),
                        c.getMinPercentageRequired(), c.getDepartment(), c.getDescription()));
            }
            default -> { System.out.println("  [!] Invalid choice."); return; }
        }

        // Ensure data/ directory exists
        new File("data").mkdirs();

        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            for (String line : lines) pw.println(line);
            System.out.println("  [✓] Exported " + (lines.size() - 1) + " records to " + filename);
        } catch (IOException e) {
            System.out.println("  [✗] Export failed: " + e.getMessage());
        }
    }

    // ── 20. Change Admin Password ─────────────────────────────────────────────
    static void changeAdminPassword() {
        System.out.println("\n  ── Change Admin Password ──");
        System.out.print("  Current password: ");
        String current = sc.nextLine().trim();
        if (!ADMIN_PASS.equals(current)) {
            System.out.println("  [✗] Incorrect current password."); return;
        }
        System.out.print("  New password: ");
        String newPass = sc.nextLine().trim();
        System.out.print("  Confirm new password: ");
        String confirm = sc.nextLine().trim();
        if (!newPass.equals(confirm)) {
            System.out.println("  [✗] Passwords do not match."); return;
        }
        if (newPass.length() < 4) {
            System.out.println("  [✗] Password must be at least 4 characters."); return;
        }
        ADMIN_PASS = newPass;

        // Also update the .env file
        try {
            updateEnvFile("ADMIN_PASSWORD", newPass);
            System.out.println("  [✓] Admin password changed and saved to .env");
        } catch (IOException e) {
            System.out.println("  [✓] Password changed for this session (could not update .env: " + e.getMessage() + ")");
        }
    }

    // ── Student Portal: Fee Calculator ────────────────────────────────────────
    static void feeCalculator() {
        System.out.println("\n  ── Fee Calculator ──");
        System.out.print("  Course ID: ");
        String id = sc.nextLine().trim().toUpperCase();
        FeeStructure fs = feeService.getFeeStructureByCourseId(id);
        if (fs == null) { System.out.println("  [!] Fee structure not found."); return; }

        int semesters = readInt("  Number of semesters to calculate: ");
        System.out.print("  Include Hostel? (y/n): ");
        boolean hostel = sc.nextLine().trim().toLowerCase().startsWith("y");
        System.out.print("  Include Transport? (y/n): ");
        boolean transport = sc.nextLine().trim().toLowerCase().startsWith("y");

        double semFee     = fs.getTotalSemesterFee(hostel, transport);
        double totalFee   = semFee * semesters + fs.getAdmissionFee();

        System.out.println("\n  ╔════════════════════════════════════════════════════╗");
        System.out.println("  ║              FEE CALCULATION SUMMARY              ║");
        System.out.println("  ╠════════════════════════════════════════════════════╣");
        System.out.printf("  ║  Course           : %-30s ║%n", fs.getCourseName());
        System.out.printf("  ║  Semesters        : %-30d ║%n", semesters);
        System.out.printf("  ║  Admission Fee    : Rs. %-26.2f ║%n", fs.getAdmissionFee());
        System.out.printf("  ║  Per Semester      : Rs. %-26.2f ║%n", semFee);
        System.out.printf("  ║  Hostel            : %-30s ║%n", hostel ? "Included" : "Not included");
        System.out.printf("  ║  Transport         : %-30s ║%n", transport ? "Included" : "Not included");
        System.out.println("  ╠════════════════════════════════════════════════════╣");
        System.out.printf("  ║  TOTAL ESTIMATED   : Rs. %-26.2f ║%n", totalFee);
        System.out.println("  ╚════════════════════════════════════════════════════╝");
    }

    // ── Student Portal: Check Scholarship Eligibility ─────────────────────────
    static void checkScholarshipEligibility() {
        System.out.println("\n  ── Check Scholarship Eligibility ──");
        System.out.print("  Enter your Application ID: ");
        String appId = sc.nextLine().trim();
        Applicant app = admissionService.getById(appId);
        if (app == null) { System.out.println("  [!] Application not found."); return; }

        double score = app.getPreviousPercentage();
        String slab;
        double waiver;
        if      (score >= 95) { slab = "GOLD";   waiver = 50; }
        else if (score >= 90) { slab = "SILVER"; waiver = 30; }
        else if (score >= 85) { slab = "BRONZE"; waiver = 15; }
        else if (score >= 80) { slab = "MERIT";  waiver = 10; }
        else                  { slab = "NONE";   waiver = 0;  }

        System.out.println("\n  ╔════════════════════════════════════════════════════╗");
        System.out.println("  ║          SCHOLARSHIP ELIGIBILITY RESULT           ║");
        System.out.println("  ╠════════════════════════════════════════════════════╣");
        System.out.printf("  ║  Name              : %-28s  ║%n", app.getName());
        System.out.printf("  ║  Previous Score    : %-28.2f  ║%n", score);
        System.out.printf("  ║  Scholarship Slab  : %-28s  ║%n", slab);
        System.out.printf("  ║  Tuition Waiver    : %-28.0f%%  ║%n", waiver);
        System.out.println("  ╚════════════════════════════════════════════════════╝");
    }

    // ── Helper: update a key in .env file ─────────────────────────────────────
    static void updateEnvFile(String key, String newValue) throws IOException {
        String[] paths = { ".env", "../.env", "../../.env" };
        File envFile = null;
        for (String p : paths) {
            File f = new File(p);
            if (f.exists()) { envFile = f; break; }
        }
        if (envFile == null) throw new IOException(".env file not found");

        List<String> lines = new ArrayList<>();
        boolean found = false;
        try (BufferedReader br = new BufferedReader(new FileReader(envFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().startsWith(key + "=")) {
                    lines.add(key + "=" + newValue);
                    found = true;
                } else {
                    lines.add(line);
                }
            }
        }
        if (!found) lines.add(key + "=" + newValue);

        try (PrintWriter pw = new PrintWriter(new FileWriter(envFile))) {
            for (String l : lines) pw.println(l);
        }
    }

    // ── Helper: read double with optional default if user presses Enter ───────
    static double readDoubleOpt(String prompt, double defaultVal) {
        System.out.print(prompt);
        String input = sc.nextLine().trim();
        if (input.isEmpty()) return defaultVal;
        try { return Double.parseDouble(input); }
        catch (NumberFormatException e) { return defaultVal; }
    }
}
