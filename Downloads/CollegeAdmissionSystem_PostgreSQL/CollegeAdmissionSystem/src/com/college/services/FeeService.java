package com.college.services;

import com.college.database.DBConnection;
import com.college.exceptions.PaymentException;
import com.college.interfaces.Manageable;
import com.college.interfaces.Payable;
import com.college.models.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/**
 * FeeService — implements Manageable<Payment> and Payable.
 * All data persisted in PostgreSQL via JDBC.
 */
public class FeeService implements Manageable<Payment>, Payable {

    private int paymentCounter = -1;
    private int txnCounter     = -1;

    // Context for Payable interface
    private String currentStudentId;
    private String currentStudentName;
    private String currentCourseId;
    private String currentSemester;

    // ── Counter helpers ───────────────────────────────────────────────────────

    private int nextPaymentNum() throws SQLException {
        if (paymentCounter < 0) {
            String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(payment_id FROM 4) AS INT)), 5000) FROM payments";
            try (Statement st = DBConnection.getConnection().createStatement();
                 ResultSet rs = st.executeQuery(sql)) {
                paymentCounter = rs.next() ? rs.getInt(1) : 5000;
            }
        }
        return ++paymentCounter;
    }

    private int nextTxnNum() throws SQLException {
        if (txnCounter < 0) {
            String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(transaction_id FROM 4) AS INT)), 9000) FROM payments WHERE transaction_id ~ '^TXN[0-9]+$'";
            try (Statement st = DBConnection.getConnection().createStatement();
                 ResultSet rs = st.executeQuery(sql)) {
                txnCounter = rs.next() ? rs.getInt(1) : 9000;
            }
        }
        return ++txnCounter;
    }

    // ── Payable Context ───────────────────────────────────────────────────────

    public void setPaymentContext(String studentId, String studentName,
                                  String courseId, String semester) {
        this.currentStudentId   = studentId;
        this.currentStudentName = studentName;
        this.currentCourseId    = courseId;
        this.currentSemester    = semester;
    }

    // ── Payable Interface ─────────────────────────────────────────────────────

    @Override
    public double calculateTotalFee(boolean includeHostel, boolean includeTransport) {
        FeeStructure fs = getFeeStructureByCourseId(currentCourseId);
        return fs == null ? 0.0 : fs.getTotalSemesterFee(includeHostel, includeTransport);
    }

    @Override
    public boolean processPayment(double amount, String paymentMethod, String paymentType) {
        try {
            pay(currentStudentId, currentStudentName, currentCourseId,
                amount, paymentType, paymentMethod, currentSemester);
            return true;
        } catch (PaymentException e) {
            System.out.println("  [Error] " + e.getMessage());
            return false;
        }
    }

    // ── Core Payment ──────────────────────────────────────────────────────────

    public Payment pay(String studentId, String studentName, String courseId,
                       double amount, String paymentType, String paymentMethod,
                       String semester) throws PaymentException {
        if (amount <= 0)
            throw new PaymentException("Invalid amount: " + amount, "PAY_001", amount);

        FeeStructure fs = getFeeStructureByCourseId(courseId);
        if (fs == null)
            throw new PaymentException("Fee structure not found for course: " + courseId, "PAY_002", amount);

        String sql = "INSERT INTO payments " +
            "(payment_id, student_id, student_name, course_id, amount, payment_date," +
            " payment_type, payment_method, semester, transaction_id, status)" +
            " VALUES (?,?,?,?,?,?,?,?,?,?,?)";

        try {
            String paymentId = "PAY" + nextPaymentNum();
            String txnId     = "TXN" + nextTxnNum() + (System.currentTimeMillis() % 10000);
            String date      = LocalDate.now().toString();

            try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
                ps.setString(1,  paymentId);
                ps.setString(2,  studentId);
                ps.setString(3,  studentName);
                ps.setString(4,  courseId);
                ps.setDouble(5,  amount);
                ps.setDate(6,    java.sql.Date.valueOf(date));
                ps.setString(7,  paymentType);
                ps.setString(8,  paymentMethod);
                ps.setString(9,  semester);
                ps.setString(10, txnId);
                ps.setString(11, "SUCCESS");
                ps.executeUpdate();
            }
            return getById(paymentId);

        } catch (SQLException e) {
            throw new PaymentException("Database error: " + e.getMessage(), "PAY_DB", amount);
        }
    }

    // ── Fee Structure Queries ─────────────────────────────────────────────────

    public FeeStructure getFeeStructureByCourseId(String courseId) {
        if (courseId == null) return null;
        String sql = "SELECT * FROM fee_structures WHERE course_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, courseId.toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapFeeStructure(rs);
            }
        } catch (SQLException e) { System.err.println("  [DB Error] " + e.getMessage()); }
        return null;
    }

    public List<FeeStructure> getAllFeeStructures() {
        List<FeeStructure> list = new ArrayList<>();
        String sql = "SELECT * FROM fee_structures ORDER BY course_id";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapFeeStructure(rs));
        } catch (SQLException e) { System.err.println("  [DB Error] " + e.getMessage()); }
        return list;
    }

    public void addFeeStructure(FeeStructure fs) {
        String sql = "INSERT INTO fee_structures " +
            "(course_id, course_name, admission_fee, tuition_fee_per_semester," +
            " library_fee_per_semester, lab_fee_per_semester, exam_fee_per_semester," +
            " hostel_fee_per_year, transport_fee_per_year)" +
            " VALUES (?,?,?,?,?,?,?,?,?) ON CONFLICT (course_id) DO NOTHING";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, fs.getCourseId());
            ps.setString(2, fs.getCourseName());
            ps.setDouble(3, fs.getAdmissionFee());
            ps.setDouble(4, fs.getTuitionFeePerSemester());
            ps.setDouble(5, fs.getLibraryFeePerSemester());
            ps.setDouble(6, fs.getLabFeePerSemester());
            ps.setDouble(7, fs.getExamFeePerSemester());
            ps.setDouble(8, fs.getHostelFeePerYear());
            ps.setDouble(9, fs.getTransportFeePerYear());
            ps.executeUpdate();
        } catch (SQLException e) { System.err.println("  [DB Error] " + e.getMessage()); }
    }

    // ── Update Fee Structure ──────────────────────────────────────────────────

    public void updateFeeStructure(String courseId, double admissionFee, double tuitionFee,
                                   double libraryFee, double labFee, double examFee,
                                   double hostelFee, double transportFee) {
        String sql = "UPDATE fee_structures SET admission_fee=?, tuition_fee_per_semester=?," +
            " library_fee_per_semester=?, lab_fee_per_semester=?, exam_fee_per_semester=?," +
            " hostel_fee_per_year=?, transport_fee_per_year=? WHERE course_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setDouble(1, admissionFee);
            ps.setDouble(2, tuitionFee);
            ps.setDouble(3, libraryFee);
            ps.setDouble(4, labFee);
            ps.setDouble(5, examFee);
            ps.setDouble(6, hostelFee);
            ps.setDouble(7, transportFee);
            ps.setString(8, courseId);
            ps.executeUpdate();
        } catch (SQLException e) { System.err.println("  [DB Error] " + e.getMessage()); }
    }

    // ── Payment Queries ───────────────────────────────────────────────────────

    public List<Payment> getPaymentsByStudentId(String studentId) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE student_id=? ORDER BY payment_date DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapPayment(rs));
            }
        } catch (SQLException e) { System.err.println("  [DB Error] " + e.getMessage()); }
        return list;
    }

    public double getTotalPaidByStudent(String studentId) {
        String sql = "SELECT COALESCE(SUM(amount),0) FROM payments WHERE student_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) { System.err.println("  [DB Error] " + e.getMessage()); }
        return 0.0;
    }

    // ── Report Queries (used by ReportService) ────────────────────────────────

    public double getTotalCollected() {
        String sql = "SELECT COALESCE(SUM(amount),0) FROM payments WHERE status='SUCCESS'";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { System.err.println("  [DB Error] " + e.getMessage()); }
        return 0.0;
    }

    public Map<String,Double> getCollectionByType() {
        Map<String,Double> map = new LinkedHashMap<>();
        String sql = "SELECT payment_type, SUM(amount) FROM payments GROUP BY payment_type ORDER BY payment_type";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) map.put(rs.getString(1), rs.getDouble(2));
        } catch (SQLException e) { System.err.println("  [DB Error] " + e.getMessage()); }
        return map;
    }

    public Map<String,Double> getCollectionByMethod() {
        Map<String,Double> map = new LinkedHashMap<>();
        String sql = "SELECT payment_method, SUM(amount) FROM payments GROUP BY payment_method ORDER BY payment_method";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) map.put(rs.getString(1), rs.getDouble(2));
        } catch (SQLException e) { System.err.println("  [DB Error] " + e.getMessage()); }
        return map;
    }

    // ── Manageable<Payment> ───────────────────────────────────────────────────

    @Override public void add(Payment p) { /* use pay() */ }

    @Override
    public Payment getById(String id) {
        String sql = "SELECT * FROM payments WHERE payment_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return mapPayment(rs); }
        } catch (SQLException e) { System.err.println("  [DB Error] " + e.getMessage()); }
        return null;
    }

    @Override
    public List<Payment> getAll() {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payments ORDER BY payment_date DESC";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapPayment(rs));
        } catch (SQLException e) { System.err.println("  [DB Error] " + e.getMessage()); }
        return list;
    }

    @Override public boolean remove(String id) {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "DELETE FROM payments WHERE payment_id=?")) {
            ps.setString(1, id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("  [DB Error] " + e.getMessage()); return false; }
    }

    @Override public boolean update(Payment p) {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "UPDATE payments SET status=? WHERE payment_id=?")) {
            ps.setString(1, p.getStatus()); ps.setString(2, p.getPaymentId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("  [DB Error] " + e.getMessage()); return false; }
    }

    // ── Mappers ───────────────────────────────────────────────────────────────

    private FeeStructure mapFeeStructure(ResultSet rs) throws SQLException {
        return new FeeStructure(
            rs.getString("course_id"),   rs.getString("course_name"),
            rs.getDouble("admission_fee"), rs.getDouble("tuition_fee_per_semester"),
            rs.getDouble("library_fee_per_semester"), rs.getDouble("lab_fee_per_semester"),
            rs.getDouble("exam_fee_per_semester"), rs.getDouble("hostel_fee_per_year"),
            rs.getDouble("transport_fee_per_year")
        );
    }

    private Payment mapPayment(ResultSet rs) throws SQLException {
        Payment p = new Payment(
            rs.getString("payment_id"),   rs.getString("student_id"),
            rs.getString("student_name"), rs.getString("course_id"),
            rs.getDouble("amount"),       rs.getString("payment_date"),
            rs.getString("payment_type"), rs.getString("payment_method"),
            rs.getString("semester"),     rs.getString("transaction_id")
        );
        p.setStatus(rs.getString("status"));
        return p;
    }
}
