package com.campushire.dao;

import com.campushire.config.DatabaseConfig;
import com.campushire.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDao {

    public List<Student> findAll() throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY cgpa DESC, full_name ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public Student findById(int id) throws SQLException {
        String sql = "SELECT * FROM students WHERE id = ?";
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

    public Student findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM students WHERE email = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public Student register(Student s) throws SQLException {
        String sql = "INSERT INTO students (full_name, email, password, roll_no, degree, department, batch_year, cgpa, skills, verification_status, profile_score) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'PENDING', 75)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getFullName());
            ps.setString(2, s.getEmail());
            ps.setString(3, s.getPassword());
            ps.setString(4, s.getRollNo());
            ps.setString(5, s.getDegree() != null ? s.getDegree() : "B.Tech");
            ps.setString(6, s.getDepartment() != null ? s.getDepartment() : "CSE");
            ps.setInt(7, s.getBatchYear() > 0 ? s.getBatchYear() : 2025);
            ps.setDouble(8, s.getCgpa());
            ps.setString(9, s.getSkills() != null ? s.getSkills() : "Java, SQL");
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    s.setId(keys.getInt(1));
                }
            }
        }
        return findById(s.getId());
    }

    public boolean updateProfile(Student s) throws SQLException {
        String sql = "UPDATE students SET full_name = ?, department = ?, cgpa = ?, skills = ?, profile_score = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getFullName());
            ps.setString(2, s.getDepartment());
            ps.setDouble(3, s.getCgpa());
            ps.setString(4, s.getSkills());
            ps.setInt(5, s.getProfileScore());
            ps.setInt(6, s.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean setVerificationStatus(int studentId, String status) throws SQLException {
        String sql = "UPDATE students SET verification_status = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, studentId);
            return ps.executeUpdate() > 0;
        }
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getInt("id"));
        s.setFullName(rs.getString("full_name"));
        s.setEmail(rs.getString("email"));
        s.setPassword(rs.getString("password"));
        s.setRollNo(rs.getString("roll_no"));
        s.setDegree(rs.getString("degree"));
        s.setDepartment(rs.getString("department"));
        s.setBatchYear(rs.getInt("batch_year"));
        s.setCgpa(rs.getDouble("cgpa"));
        s.setSkills(rs.getString("skills"));
        s.setResumeUrl(rs.getString("resume_url"));
        s.setVerificationStatus(rs.getString("verification_status"));
        s.setProfileScore(rs.getInt("profile_score"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        return s;
    }
}
