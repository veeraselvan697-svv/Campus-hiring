package com.campushire.controller;

import com.campushire.dao.StudentDao;
import com.campushire.model.Student;
import com.campushire.util.HttpUtil;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;

public class StudentController implements HttpHandler {
    private final StudentDao studentDao = new StudentDao();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (HttpUtil.handlePreflight(exchange)) return;

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {
            if ("GET".equalsIgnoreCase(method)) {
                String[] segments = path.split("/");
                if (segments.length > 3 && !segments[3].isEmpty()) {
                    int id = Integer.parseInt(segments[3]);
                    Student s = studentDao.findById(id);
                    if (s != null) {
                        HttpUtil.sendJsonResponse(exchange, 200, s);
                    } else {
                        HttpUtil.sendErrorResponse(exchange, 404, "Student not found");
                    }
                } else {
                    List<Student> students = studentDao.findAll();
                    HttpUtil.sendJsonResponse(exchange, 200, students);
                }
            } else if ("PUT".equalsIgnoreCase(method) && path.endsWith("/verify")) {
                String[] segments = path.split("/");
                int id = Integer.parseInt(segments[3]);
                String body = HttpUtil.readRequestBody(exchange);
                JsonObject json = HttpUtil.getGson().fromJson(body, JsonObject.class);
                String status = (json != null && json.has("status")) ? json.get("status").getAsString() : "VERIFIED";
                boolean updated = studentDao.setVerificationStatus(id, status);
                if (updated) {
                    HttpUtil.sendSuccessResponse(exchange, 200, "Student status updated to " + status, null);
                } else {
                    HttpUtil.sendErrorResponse(exchange, 404, "Student not found");
                }
            } else if ("PUT".equalsIgnoreCase(method)) {
                String[] segments = path.split("/");
                int id = Integer.parseInt(segments[3]);
                String body = HttpUtil.readRequestBody(exchange);
                Student s = HttpUtil.getGson().fromJson(body, Student.class);
                s.setId(id);
                boolean updated = studentDao.updateProfile(s);
                if (updated) {
                    Student fresh = studentDao.findById(id);
                    HttpUtil.sendSuccessResponse(exchange, 200, "Profile updated successfully", fresh);
                } else {
                    HttpUtil.sendErrorResponse(exchange, 404, "Student not found");
                }
            } else {
                HttpUtil.sendErrorResponse(exchange, 405, "Method not allowed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            HttpUtil.sendErrorResponse(exchange, 500, "Error handling student request: " + e.getMessage());
        }
    }
}
