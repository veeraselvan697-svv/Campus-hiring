package com.campushire.dao;

import com.campushire.config.DatabaseConfig;
import com.campushire.model.Drive;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DriveDao {

    public List<Drive> findAll(boolean activeOnly) throws SQLException {
        List<Drive> list = new ArrayList<>();
        String sql = "SELECT d.*, c.name AS company_name, c.category AS company_category, " +
                     "c.logo_abbr AS company_logo_abbr, c.color_accent AS company_color_accent, " +
                     "(SELECT COUNT(*) FROM applications a WHERE a.drive_id = d.id) AS applicant_count " +
                     "FROM drives d " +
                     "JOIN companies c ON d.company_id = c.id " +
                     (activeOnly ? "WHERE d.status = 'ACTIVE' " : "") +
                     "ORDER BY d.package_num DESC, d.id ASC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public Drive findById(int id) throws SQLException {
        String sql = "SELECT d.*, c.name AS company_name, c.category AS company_category, " +
                     "c.logo_abbr AS company_logo_abbr, c.color_accent AS company_color_accent, " +
                     "(SELECT COUNT(*) FROM applications a WHERE a.drive_id = d.id) AS applicant_count " +
                     "FROM drives d " +
                     "JOIN companies c ON d.company_id = c.id " +
                     "WHERE d.id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public Drive createDrive(Drive d) throws SQLException {
        String sql = "INSERT INTO drives (company_id, role_title, location, package_ctc, package_num, min_cgpa, eligible_branches, required_skills, deadline_days, status, openings) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVE', ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, d.getCompanyId());
            ps.setString(2, d.getRoleTitle());
            ps.setString(3, d.getLocation() != null ? d.getLocation() : "India");
            ps.setString(4, d.getPackageCtc());
            ps.setDouble(5, d.getPackageNum() > 0 ? d.getPackageNum() : 7.0);
            ps.setDouble(6, d.getMinCgpa() > 0 ? d.getMinCgpa() : 7.0);
            ps.setString(7, d.getEligibleBranches() != null ? d.getEligibleBranches() : "CSE, IT, ECE");
            ps.setString(8, d.getRequiredSkills() != null ? d.getRequiredSkills() : "Problem Solving");
            ps.setString(9, d.getDeadlineDays() != null ? d.getDeadlineDays() : "7 Days Left");
            ps.setInt(10, d.getOpenings() > 0 ? d.getOpenings() : 30);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    d.setId(keys.getInt(1));
                }
            }
        }
        return findById(d.getId());
    }

    public boolean updateStatus(int driveId, String status) throws SQLException {
        String sql = "UPDATE drives SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, driveId);
            return ps.executeUpdate() > 0;
        }
    }

    private Drive mapRow(ResultSet rs) throws SQLException {
        Drive d = new Drive();
        d.setId(rs.getInt("id"));
        d.setCompanyId(rs.getInt("company_id"));
        d.setCompanyName(rs.getString("company_name"));
        d.setCompanyCategory(rs.getString("company_category"));
        d.setCompanyLogoAbbr(rs.getString("company_logo_abbr"));
        d.setColorAccent(rs.getString("company_color_accent"));
        d.setRoleTitle(rs.getString("role_title"));
        d.setLocation(rs.getString("location"));
        d.setPackageCtc(rs.getString("package_ctc"));
        d.setPackageNum(rs.getDouble("package_num"));
        d.setMinCgpa(rs.getDouble("min_cgpa"));
        d.setEligibleBranches(rs.getString("eligible_branches"));
        d.setRequiredSkills(rs.getString("required_skills"));
        d.setDeadlineDays(rs.getString("deadline_days"));
        d.setStatus(rs.getString("status"));
        d.setOpenings(rs.getInt("openings"));
        d.setApplicantCount(rs.getInt("applicant_count"));
        d.setCreatedAt(rs.getTimestamp("created_at"));
        return d;
    }
}
