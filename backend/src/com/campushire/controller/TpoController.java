package com.campushire.controller;

import com.campushire.config.DatabaseConfig;
import com.campushire.dao.DriveDao;
import com.campushire.dao.TpoDao;
import com.campushire.model.Announcement;
import com.campushire.model.Drive;
import com.campushire.model.Interview;
import com.campushire.model.Student;
import com.campushire.util.HttpUtil;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TpoController implements HttpHandler {
    private final TpoDao tpoDao = new TpoDao();
    private final DriveDao driveDao = new DriveDao();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (HttpUtil.handlePreflight(exchange)) return;

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {
            if (path.endsWith("/stats") && "GET".equalsIgnoreCase(method)) {
                Map<String, Object> stats = tpoDao.getDashboardStats();
                HttpUtil.sendJsonResponse(exchange, 200, stats);
            } else if (path.endsWith("/announcements") && "GET".equalsIgnoreCase(method)) {
                List<Announcement> list = getAnnouncements();
                HttpUtil.sendJsonResponse(exchange, 200, list);
            } else if (path.endsWith("/announcements") && "POST".equalsIgnoreCase(method)) {
                String body = HttpUtil.readRequestBody(exchange);
                JsonObject json = HttpUtil.getGson().fromJson(body, JsonObject.class);
                String title = json.has("title") ? json.get("title").getAsString() : "New Notice";
                String content = json.has("content") ? json.get("content").getAsString() : "";
                String targetBranch = json.has("targetBranch") ? json.get("targetBranch").getAsString() : "ALL";
                createAnnouncement(title, content, targetBranch);
                HttpUtil.sendSuccessResponse(exchange, 201, "Announcement published successfully", null);
            } else if (path.endsWith("/interviews") && "GET".equalsIgnoreCase(method)) {
                List<Interview> interviews = getInterviews();
                HttpUtil.sendJsonResponse(exchange, 200, interviews);
            } else if (path.contains("/export/eligibility") && "GET".equalsIgnoreCase(method)) {
                handleExportEligibilityCsv(exchange);
            } else {
                HttpUtil.sendErrorResponse(exchange, 404, "Endpoint not found: " + path);
            }
        } catch (Exception e) {
            e.printStackTrace();
            HttpUtil.sendErrorResponse(exchange, 500, "TPO Operations error: " + e.getMessage());
        }
    }

    private void handleExportEligibilityCsv(HttpExchange exchange) throws IOException, SQLException {
        Map<String, String> params = HttpUtil.parseQueryParams(exchange);
        int driveId = params.containsKey("driveId") ? Integer.parseInt(params.get("driveId")) : 1;
        Drive drive = driveDao.findById(driveId);
        List<Student> eligible = tpoDao.getEligibleStudentsForDrive(driveId);

        String companyName = drive != null ? drive.getCompanyName() : "CampusDrive";
        String filename = "CampusHire_Eligible_List_" + companyName.replaceAll("\\s+", "_") + ".csv";

        HttpUtil.setCorsHeaders(exchange);
        exchange.getResponseHeaders().set("Content-Type", "text/csv; charset=UTF-8");
        exchange.getResponseHeaders().set("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        StringBuilder sb = new StringBuilder();
        sb.append("CampusHire Institutional Placement Cell - Eligibility & Audit Export\n");
        sb.append("Drive: ").append(drive != null ? drive.getRoleTitle() + " at " + drive.getCompanyName() : "Placement Drive").append("\n");
        sb.append("Min CGPA Cutoff: ").append(drive != null ? drive.getMinCgpa() : "7.0").append("\n");
        sb.append("Package: ").append(drive != null ? drive.getPackageCtc() : "N/A").append("\n");
        sb.append("Generated On: ").append(new java.util.Date()).append("\n\n");
        sb.append("Student ID,Full Name,Roll Number,Department,CGPA,Verification Status,Skills\n");

        for (Student s : eligible) {
            sb.append(s.getId()).append(",")
              .append("\"").append(s.getFullName()).append("\",")
              .append("\"").append(s.getRollNo()).append("\",")
              .append("\"").append(s.getDepartment()).append("\",")
              .append(s.getCgpa()).append(",")
              .append("\"").append(s.getVerificationStatus()).append("\",")
              .append("\"").append(s.getSkills() != null ? s.getSkills().replace("\"", "\"\"") : "").append("\"\n");
        }

        byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private List<Announcement> getAnnouncements() throws SQLException {
        List<Announcement> list = new ArrayList<>();
        String sql = "SELECT * FROM announcements ORDER BY created_at DESC LIMIT 10";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Announcement a = new Announcement();
                a.setId(rs.getInt("id"));
                a.setTitle(rs.getString("title"));
                a.setContent(rs.getString("content"));
                a.setTargetBranch(rs.getString("target_branch"));
                a.setBadge(rs.getString("badge"));
                a.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(a);
            }
        }
        return list;
    }

    private void createAnnouncement(String title, String content, String branch) throws SQLException {
        String sql = "INSERT INTO announcements (title, content, target_branch, badge) VALUES (?, ?, ?, 'ALERT')";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, content);
            ps.setString(3, branch);
            ps.executeUpdate();
        }
    }

    private List<Interview> getInterviews() throws SQLException {
        List<Interview> list = new ArrayList<>();
        String sql = "SELECT i.*, c.name AS company_name, d.role_title " +
                     "FROM interviews i " +
                     "JOIN applications a ON i.application_id = a.id " +
                     "JOIN drives d ON a.drive_id = d.id " +
                     "JOIN companies c ON d.company_id = c.id " +
                     "ORDER BY i.id ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Interview inv = new Interview();
                inv.setId(rs.getInt("id"));
                inv.setApplicationId(rs.getInt("application_id"));
                inv.setRoundName(rs.getString("round_name"));
                inv.setScheduledTime(rs.getString("scheduled_time"));
                inv.setMeetingLink(rs.getString("meeting_link"));
                inv.setStatus(rs.getString("status"));
                inv.setCompanyName(rs.getString("company_name"));
                inv.setRoleTitle(rs.getString("role_title"));
                list.add(inv);
            }
        }
        return list;
    }
}
