package com.college.services;

import com.college.database.DBConnection;
import com.college.exceptions.AdmissionException;
import com.college.interfaces.Manageable;
import com.college.models.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/**
 * AdmissionService — all data stored/retrieved from PostgreSQL.
 * Implements Manageable<Applicant> (generic interface).
 */
public class AdmissionService implements Manageable<Applicant> {

    private int applicantCounter = -1;
    private int studentCounter   = -1;

    // ── Counter helpers (read MAX from DB so restarts are safe) ───────────────

    private int nextApplicantNum() throws SQLException {
        if (applicantCounter < 0) {
            String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(application_id FROM 4) AS INT)), 1000) FROM applicants";
            try (Statement st = DBConnection.getConnection().createStatement();
                 ResultSet rs = st.executeQuery(sql)) {
                if (rs.next()) applicantCounter = rs.getInt(1);
                else           applicantCounter = 1000;
            }
        }
        return ++applicantCounter;
    }

    private int nextStudentNum() throws SQLException {
        if (studentCounter < 0) {
            String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(student_id FROM 4) AS INT)), 2000) FROM students";
            try (Statement st = DBConnection.getConnection().createStatement();
                 ResultSet rs = st.executeQuery(sql)) {
                if (rs.next()) studentCounter = rs.getInt(1);
                else           studentCounter = 2000;
            }
        }
        return ++studentCounter;
    }

    // ── Admission Operations ──────────────────────────────────────────────────

    public Applicant applyForAdmission(String name, String email, String phone, String address,
                                       String gender, String dob, String courseId,
                                       String prevInstitution, double prevPercentage)
            throws AdmissionException {
        try {
            Course course = getCourseById(courseId);
            if (course == null)
                throw new AdmissionException("Course '" + courseId + "' not found.", "ADM_001");
            if (!course.hasAvailableSeats())
                throw new AdmissionException("No seats available for " + course.getCourseName(), "ADM_002");
            if (prevPercentage < course.getMinPercentageRequired())
                throw new AdmissionException(
                    String.format("Not eligible. Required: %.1f%%, Your score: %.1f%%",
                        course.getMinPercentageRequired(), prevPercentage), "ADM_003");

            String appId = "APP" + nextApplicantNum();
            String sql = "INSERT INTO applicants " +
                "(application_id, full_name, email, phone, address, gender, date_of_birth," +
                " applied_course_id, applied_course_name, previous_institution," +
                " previous_percentage, application_date, application_status, remarks)" +
                " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
                ps.setString(1,  appId);
                ps.setString(2,  name);
                ps.setString(3,  email);
                ps.setString(4,  phone);
                ps.setString(5,  address);
                ps.setString(6,  gender);
                ps.setString(7,  dob);
                ps.setString(8,  courseId.toUpperCase());
                ps.setString(9,  course.getCourseName());
                ps.setString(10, prevInstitution);
                ps.setDouble(11, prevPercentage);
                ps.setDate(12,   java.sql.Date.valueOf(LocalDate.now()));
                ps.setString(13, "PENDING");
                ps.setString(14, "-");
                ps.executeUpdate();
            }
            return getById(appId);

        } catch (SQLException e) {
            throw new AdmissionException("Database error: " + e.getMessage(), "ADM_DB");
        }
    }

    public Student approveApplication(String applicationId) throws AdmissionException {
        try {
            Applicant app = getById(applicationId);
            if (app == null)
                throw new AdmissionException("Application not found: " + applicationId, "ADM_004");
            if (!"PENDING".equals(app.getApplicationStatus()))
                throw new AdmissionException("Application is already " + app.getApplicationStatus(), "ADM_005");

            Course course = getCourseById(app.getAppliedCourseId());
            if (course == null)
                throw new AdmissionException("Course not found.", "ADM_006");
            if (!course.hasAvailableSeats())
                throw new AdmissionException("No seats available!", "ADM_007");

            Connection conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try {
                // 1. Update applicant
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE applicants SET application_status=?, remarks=? WHERE application_id=?")) {
                    ps.setString(1, "APPROVED");
                    ps.setString(2, "Admission granted.");
                    ps.setString(3, applicationId);
                    ps.executeUpdate();
                }

                // 2. Decrement seats
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE courses SET available_seats = available_seats - 1 WHERE course_id=? AND available_seats > 0")) {
                    ps.setString(1, course.getCourseId());
                    ps.executeUpdate();
                }

                // 3. Create student
                String studentId = "STU" + nextStudentNum();
                String year = String.valueOf(LocalDate.now().getYear());
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO students " +
                        "(student_id, full_name, email, phone, address, gender, date_of_birth," +
                        " enrolled_course, course_id, semester, cgpa, admission_year, status)" +
                        " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)")) {
                    ps.setString(1,  studentId);
                    ps.setString(2,  app.getName());
                    ps.setString(3,  app.getEmail());
                    ps.setString(4,  app.getPhone());
                    ps.setString(5,  app.getAddress());
                    ps.setString(6,  app.getGender());
                    ps.setString(7,  app.getDateOfBirth());
                    ps.setString(8,  course.getCourseName());
                    ps.setString(9,  course.getCourseId());
                    ps.setInt(10,    1);
                    ps.setDouble(11, 0.0);
                    ps.setString(12, year);
                    ps.setString(13, "ACTIVE");
                    ps.executeUpdate();
                }

                conn.commit();
                conn.setAutoCommit(true);
                return getStudentById(studentId);

            } catch (SQLException e) {
                conn.rollback();
                conn.setAutoCommit(true);
                throw e;
            }
        } catch (SQLException e) {
            throw new AdmissionException("Database error: " + e.getMessage(), "ADM_DB");
        }
    }

    public void rejectApplication(String applicationId, String reason) throws AdmissionException {
        try {
            Applicant app = getById(applicationId);
            if (app == null)
                throw new AdmissionException("Application not found: " + applicationId, "ADM_004");
            if (!"PENDING".equals(app.getApplicationStatus()))
                throw new AdmissionException("Application is already " + app.getApplicationStatus(), "ADM_005");

            String sql = "UPDATE applicants SET application_status=?, remarks=? WHERE application_id=?";
            try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
                ps.setString(1, "REJECTED");
                ps.setString(2, (reason == null || reason.isBlank()) ? "Rejected by admin." : reason);
                ps.setString(3, applicationId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new AdmissionException("Database error: " + e.getMessage(), "ADM_DB");
        }
    }

    // ── Query: Applicants ─────────────────────────────────────────────────────

    public List<Applicant> getPendingApplications() { return getByStatus("PENDING"); }

    public List<Applicant> getByStatus(String status) {
        List<Applicant> list = new ArrayList<>();
        String sql = "SELECT * FROM applicants WHERE application_status=? ORDER BY application_id";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapApplicant(rs));
            }
        } catch (SQLException e) { System.err.println("  [DB Error] " + e.getMessage()); }
        return list;
    }

    // ── Query: Students ───────────────────────────────────────────────────────

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY student_id";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapStudent(rs));
        } catch (SQLException e) { System.err.println("  [DB Error] " + e.getMessage()); }
        return list;
    }

    public Student getStudentById(String id) {
        String sql = "SELECT * FROM students WHERE student_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapStudent(rs);
            }
        } catch (SQLException e) { System.err.println("  [DB Error] " + e.getMessage()); }
        return null;
    }

    // ── Query: Courses ────────────────────────────────────────────────────────

    public List<Course> getAllCourses() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM courses ORDER BY course_id";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapCourse(rs));
        } catch (SQLException e) { System.err.println("  [DB Error] " + e.getMessage()); }
        return list;
    }

    public Course getCourseById(String id) {
        if (id == null) return null;
        String sql = "SELECT * FROM courses WHERE course_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, id.toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapCourse(rs);
            }
        } catch (SQLException e) { System.err.println("  [DB Error] " + e.getMessage()); }
        return null;
    }

    public void addCourse(Course c) {
        String sql = "INSERT INTO courses " +
            "(course_id, course_name, course_type, duration_years, total_seats," +
            " available_seats, min_percentage_required, department, description)" +
            " VALUES (?,?,?,?,?,?,?,?,?) ON CONFLICT (course_id) DO NOTHING";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, c.getCourseId());
            ps.setString(2, c.getCourseName());
            ps.setString(3, c.getCourseType());
            ps.setInt(4,    c.getDuration());
            ps.setInt(5,    c.getTotalSeats());
            ps.setInt(6,    c.getAvailableSeats());
            ps.setDouble(7, c.getMinPercentageRequired());
            ps.setString(8, c.getDepartment());
            ps.setString(9, c.getDescription());
            ps.executeUpdate();
        } catch (SQLException e) { System.err.println("  [DB Error] " + e.getMessage()); }
    }

    // ── Manageable<Applicant> ─────────────────────────────────────────────────

    @Override public void add(Applicant a)       { System.out.println("  Use applyForAdmission() for full validation."); }

    @Override
    public Applicant getById(String id) {
        String sql = "SELECT * FROM applicants WHERE application_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return mapApplicant(rs); }
        } catch (SQLException e) { System.err.println("  [DB Error] " + e.getMessage()); }
        return null;
    }

    @Override public List<Applicant> getAll() {
        List<Applicant> list = new ArrayList<>();
        String sql = "SELECT * FROM applicants ORDER BY application_id";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapApplicant(rs));
        } catch (SQLException e) { System.err.println("  [DB Error] " + e.getMessage()); }
        return list;
    }

    @Override public boolean remove(String id) {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "DELETE FROM applicants WHERE application_id=?")) {
            ps.setString(1, id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("  [DB Error] " + e.getMessage()); return false; }
    }

    @Override public boolean update(Applicant a) {
        String sql = "UPDATE applicants SET full_name=?, email=?, phone=?, application_status=?, remarks=? WHERE application_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, a.getName()); ps.setString(2, a.getEmail()); ps.setString(3, a.getPhone());
            ps.setString(4, a.getApplicationStatus()); ps.setString(5, a.getRemarks()); ps.setString(6, a.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("  [DB Error] " + e.getMessage()); return false; }
    }

    // ── ResultSet → Model Mappers ─────────────────────────────────────────────

    private Applicant mapApplicant(ResultSet rs) throws SQLException {
        Applicant a = new Applicant(
            rs.getString("application_id"), rs.getString("full_name"),
            rs.getString("email"),          rs.getString("phone"),
            rs.getString("address"),        rs.getString("gender"),
            rs.getString("date_of_birth"),  rs.getString("applied_course_id"),
            rs.getString("applied_course_name"), rs.getString("previous_institution"),
            rs.getDouble("previous_percentage"), rs.getString("application_date")
        );
        a.setApplicationStatus(rs.getString("application_status"));
        a.setRemarks(rs.getString("remarks"));
        return a;
    }

    private Student mapStudent(ResultSet rs) throws SQLException {
        String sid = rs.getString("student_id");
        Student s = new Student(
            sid + "_ID", rs.getString("full_name"), rs.getString("email"),
            rs.getString("phone"), rs.getString("address"), rs.getString("gender"),
            rs.getString("date_of_birth"), sid, rs.getString("enrolled_course"),
            rs.getString("course_id"), rs.getInt("semester"), rs.getString("admission_year")
        );
        s.setCgpa(rs.getDouble("cgpa"));
        s.setStatus(rs.getString("status"));
        return s;
    }

    private Course mapCourse(ResultSet rs) throws SQLException {
        Course c = new Course(
            rs.getString("course_id"), rs.getString("course_name"),
            rs.getString("course_type"), rs.getInt("duration_years"),
            rs.getInt("total_seats"), rs.getDouble("min_percentage_required"),
            rs.getString("department"), rs.getString("description")
        );
        c.setAvailableSeats(rs.getInt("available_seats"));
        return c;
    }
}
