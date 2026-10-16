package com.campushire.dao;

import com.campushire.config.DatabaseConfig;
import com.campushire.model.Application;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApplicationDao {

    public List<Application> findByStudentId(int studentId) throws SQLException {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT a.*, s.full_name AS student_name, s.roll_no AS student_roll_no, s.department AS student_department, s.cgpa AS student_cgpa, " +
                     "c.name AS company_name, c.logo_abbr, c.color_accent, d.role_title, d.location, d.package_ctc " +
                     "FROM applications a " +
                     "JOIN students s ON a.student_id = s.id " +
                     "JOIN drives d ON a.drive_id = d.id " +
                     "JOIN companies c ON d.company_id = c.id " +
                     "WHERE a.student_id = ? " +
                     "ORDER BY a.applied_date DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public List<Application> findAll() throws SQLException {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT a.*, s.full_name AS student_name, s.roll_no AS student_roll_no, s.department AS student_department, s.cgpa AS student_cgpa, " +
                     "c.name AS company_name, c.logo_abbr, c.color_accent, d.role_title, d.location, d.package_ctc " +
                     "FROM applications a " +
                     "JOIN students s ON a.student_id = s.id " +
                     "JOIN drives d ON a.drive_id = d.id " +
                     "JOIN companies c ON d.company_id = c.id " +
                     "ORDER BY a.applied_date DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public boolean apply(int studentId, int driveId) throws SQLException {
        // Check if already applied
        String checkSql = "SELECT id FROM applications WHERE student_id = ? AND drive_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
            checkPs.setInt(1, studentId);
            checkPs.setInt(2, driveId);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()) {
                    return false; // Already applied
                }
            }
        }

        // Insert new application
        String insertSql = "INSERT INTO applications (student_id, drive_id, current_stage, stage_label, rounds_completed, next_step_details) " +
                           "VALUES (?, ?, 'APPLIED', 'In Review', '0/3 Complete', 'Application submitted. Profile sent to recruiter.')";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, driveId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateStage(int applicationId, String stage, String stageLabel, String roundsCompleted, String nextStepDetails, String offerPackage) throws SQLException {
        String sql = "UPDATE applications SET current_stage = ?, stage_label = ?, rounds_completed = ?, next_step_details = ?, offer_package = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, stage);
            ps.setString(2, stageLabel);
            ps.setString(3, roundsCompleted);
            ps.setString(4, nextStepDetails);
            ps.setString(5, offerPackage);
            ps.setInt(6, applicationId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean acceptOffer(int applicationId) throws SQLException {
        String sql = "UPDATE applications SET is_offer_accepted = TRUE, stage_label = 'Offer Accepted' WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, applicationId);
            return ps.executeUpdate() > 0;
        }
    }

    private Application mapRow(ResultSet rs) throws SQLException {
        Application a = new Application();
        a.setId(rs.getInt("id"));
        a.setStudentId(rs.getInt("student_id"));
        a.setDriveId(rs.getInt("drive_id"));
        a.setStudentName(rs.getString("student_name"));
        a.setStudentRollNo(rs.getString("student_roll_no"));
        a.setStudentDepartment(rs.getString("student_department"));
        a.setStudentCgpa(rs.getDouble("student_cgpa"));
        a.setCompanyName(rs.getString("company_name"));
        a.setLogoAbbr(rs.getString("logo_abbr"));
        a.setColorAccent(rs.getString("color_accent"));
        a.setRoleTitle(rs.getString("role_title"));
        a.setLocation(rs.getString("location"));
        a.setPackageCtc(rs.getString("package_ctc"));
        a.setAppliedDate(rs.getTimestamp("applied_date"));
        a.setCurrentStage(rs.getString("current_stage"));
        a.setStageLabel(rs.getString("stage_label"));
        a.setRoundsCompleted(rs.getString("rounds_completed"));
        a.setNextStepDetails(rs.getString("next_step_details"));
        a.setOfferPackage(rs.getString("offer_package"));
        a.setOfferAccepted(rs.getBoolean("is_offer_accepted"));
        return a;
    }
}
