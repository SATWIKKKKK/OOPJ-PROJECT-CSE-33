package com.college.services;

import com.college.database.DBConnection;
import com.college.models.*;

import java.sql.*;
import java.util.*;

/**
 * ReportService — generates reports using SQL aggregate queries directly.
 */
public class ReportService {

    private final AdmissionService admissionService;
    private final FeeService       feeService;

    public ReportService(AdmissionService admissionService, FeeService feeService) {
        this.admissionService = admissionService;
        this.feeService       = feeService;
    }

    // ── Admission Report ──────────────────────────────────────────────────────

    public void generateAdmissionReport() {
        try {
            Connection conn = DBConnection.getConnection();

            // Totals via SQL GROUP BY
            Map<String, Integer> statusCounts = new LinkedHashMap<>();
            String sql1 = "SELECT application_status, COUNT(*) FROM applicants GROUP BY application_status";
            try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql1)) {
                while (rs.next()) statusCounts.put(rs.getString(1), rs.getInt(2));
            }

            int totalStudents = 0;
            String sql2 = "SELECT COUNT(*) FROM students";
            try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql2)) {
                if (rs.next()) totalStudents = rs.getInt(1);
            }

            int totalApplicants = statusCounts.values().stream().mapToInt(Integer::intValue).sum();

            System.out.println("\n  ╔════════════════════════════════════════════════════╗");
            System.out.println("  ║            ADMISSION SUMMARY REPORT               ║");
            System.out.println("  ╠════════════════════════════════════════════════════╣");
            System.out.printf("  ║  Total Applications  : %-28d ║%n", totalApplicants);
            System.out.printf("  ║  Pending             : %-28d ║%n", statusCounts.getOrDefault("PENDING",  0));
            System.out.printf("  ║  Approved            : %-28d ║%n", statusCounts.getOrDefault("APPROVED", 0));
            System.out.printf("  ║  Rejected            : %-28d ║%n", statusCounts.getOrDefault("REJECTED", 0));
            System.out.printf("  ║  Enrolled Students   : %-28d ║%n", totalStudents);

            // Course-wise seat availability — direct SQL
            System.out.println("  ╠════════════════════════════════════════════════════╣");
            System.out.println("  ║           COURSE-WISE SEAT AVAILABILITY           ║");
            System.out.println("  ╠════════════════════════════════════════════════════╣");
            String sql3 = "SELECT course_name, available_seats, total_seats FROM courses ORDER BY course_id";
            try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql3)) {
                while (rs.next()) {
                    String name = rs.getString(1);
                    if (name.length() > 26) name = name.substring(0, 23) + "...";
                    System.out.printf("  ║  %-26s : %2d / %2d seats available ║%n",
                            name, rs.getInt(2), rs.getInt(3));
                }
            }

            // Department-wise student count
            System.out.println("  ╠════════════════════════════════════════════════════╣");
            System.out.println("  ║           DEPARTMENT-WISE ENROLLMENT              ║");
            System.out.println("  ╠════════════════════════════════════════════════════╣");
            String sql4 = "SELECT c.department, COUNT(s.student_id) " +
                          "FROM courses c LEFT JOIN students s ON c.course_id = s.course_id " +
                          "GROUP BY c.department ORDER BY c.department";
            try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql4)) {
                while (rs.next()) {
                    System.out.printf("  ║  %-30s : %-20d ║%n", rs.getString(1), rs.getInt(2));
                }
            }
            System.out.println("  ╚════════════════════════════════════════════════════╝");

        } catch (SQLException e) {
            System.err.println("  [Report Error] " + e.getMessage());
        }
    }

    // ── Fee Report ────────────────────────────────────────────────────────────

    public void generateFeeReport() {
        try {
            Connection conn = DBConnection.getConnection();
            long   totalTxn       = 0;
            double totalCollected = feeService.getTotalCollected();

            String sql1 = "SELECT COUNT(*) FROM payments";
            try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql1)) {
                if (rs.next()) totalTxn = rs.getLong(1);
            }

            System.out.println("\n  ╔════════════════════════════════════════════════════╗");
            System.out.println("  ║              FEE COLLECTION REPORT                ║");
            System.out.println("  ╠════════════════════════════════════════════════════╣");
            System.out.printf("  ║  Total Transactions  : %-28d ║%n", totalTxn);
            System.out.printf("  ║  Total Collected     : Rs. %-25.2f ║%n", totalCollected);

            // Payment type breakdown
            System.out.println("  ╠════════════════════════════════════════════════════╣");
            System.out.println("  ║            PAYMENT TYPE BREAKDOWN                 ║");
            System.out.println("  ╠════════════════════════════════════════════════════╣");
            for (Map.Entry<String,Double> e : feeService.getCollectionByType().entrySet())
                System.out.printf("  ║  %-18s : Rs. %-25.2f ║%n", e.getKey(), e.getValue());

            // Payment method breakdown
            System.out.println("  ╠════════════════════════════════════════════════════╣");
            System.out.println("  ║            PAYMENT METHOD BREAKDOWN               ║");
            System.out.println("  ╠════════════════════════════════════════════════════╣");
            for (Map.Entry<String,Double> e : feeService.getCollectionByMethod().entrySet())
                System.out.printf("  ║  %-18s : Rs. %-25.2f ║%n", e.getKey(), e.getValue());

            // Course-wise collection
            System.out.println("  ╠════════════════════════════════════════════════════╣");
            System.out.println("  ║            COURSE-WISE COLLECTION                 ║");
            System.out.println("  ╠════════════════════════════════════════════════════╣");
            String sql2 = "SELECT p.course_id, COALESCE(SUM(p.amount),0) " +
                          "FROM payments p GROUP BY p.course_id ORDER BY p.course_id";
            try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql2)) {
                while (rs.next())
                    System.out.printf("  ║  %-18s : Rs. %-25.2f ║%n", rs.getString(1), rs.getDouble(2));
            }
            System.out.println("  ╚════════════════════════════════════════════════════╝");

        } catch (SQLException e) {
            System.err.println("  [Report Error] " + e.getMessage());
        }
    }

    // ── Per-Student Fee Report ────────────────────────────────────────────────

    public void generateStudentFeeReport(String studentId) {
        List<Payment> pays  = feeService.getPaymentsByStudentId(studentId);
        double        total = feeService.getTotalPaidByStudent(studentId);

        System.out.println("\n  ╔════════════════════════════════════════════════════╗");
        System.out.printf("  ║  Fee History — Student: %-27s ║%n", studentId);
        System.out.println("  ╠════════════════════════════════════════════════════╣");

        if (pays.isEmpty()) {
            System.out.println("  ║  No payments found for this student.               ║");
        } else {
            System.out.println("  ║  PayID      | Type       | Method  | Amount   | Date  ║");
            System.out.println("  ╠════════════════════════════════════════════════════╣");
            for (Payment p : pays) {
                System.out.printf("  ║  %-10s | %-10s | %-7s | Rs.%-6.0f | %s ║%n",
                        p.getPaymentId(), p.getPaymentType(), p.getPaymentMethod(),
                        p.getAmount(), p.getPaymentDate());
            }
            System.out.println("  ╠════════════════════════════════════════════════════╣");
            System.out.printf("  ║  Total Paid : Rs. %-32.2f ║%n", total);
        }
        System.out.println("  ╚════════════════════════════════════════════════════╝");
    }
}
