package com.campushire.controller;

import com.campushire.dao.StudentDao;
import com.campushire.dao.TpoDao;
import com.campushire.model.Student;
import com.campushire.model.TpoOfficer;
import com.campushire.util.HttpUtil;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AuthController implements HttpHandler {
    private final StudentDao studentDao = new StudentDao();
    private final TpoDao tpoDao = new TpoDao();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (HttpUtil.handlePreflight(exchange)) return;

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {
            if ("POST".equalsIgnoreCase(method) && path.endsWith("/student/login")) {
                handleStudentLogin(exchange);
            } else if ("POST".equalsIgnoreCase(method) && path.endsWith("/tpo/login")) {
                handleTpoLogin(exchange);
            } else if ("POST".equalsIgnoreCase(method) && path.endsWith("/register")) {
                handleRegister(exchange);
            } else {
                HttpUtil.sendErrorResponse(exchange, 404, "Endpoint not found: " + path);
            }
        } catch (Exception e) {
            e.printStackTrace();
            HttpUtil.sendErrorResponse(exchange, 500, "Authentication error: " + e.getMessage());
        }
    }

    private void handleStudentLogin(HttpExchange exchange) throws IOException, SQLException {
        String body = HttpUtil.readRequestBody(exchange);
        JsonObject json = HttpUtil.getGson().fromJson(body, JsonObject.class);
        String email = json.has("email") ? json.get("email").getAsString() : "";
        String password = json.has("password") ? json.get("password").getAsString() : "";

        Student student = studentDao.findByEmail(email);
        if (student == null || !student.getPassword().equals(password)) {
            HttpUtil.sendErrorResponse(exchange, 401, "Invalid email or password");
            return;
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("role", "STUDENT");
        resp.put("token", "dummy-student-token-" + student.getId());
        resp.put("user", student);
        HttpUtil.sendSuccessResponse(exchange, 200, "Login successful", resp);
    }

    private void handleTpoLogin(HttpExchange exchange) throws IOException, SQLException {
        String body = HttpUtil.readRequestBody(exchange);
        JsonObject json = HttpUtil.getGson().fromJson(body, JsonObject.class);
        String email = json.has("email") ? json.get("email").getAsString() : "";
        String password = json.has("password") ? json.get("password").getAsString() : "";

        TpoOfficer officer = tpoDao.login(email, password);
        if (officer == null) {
            HttpUtil.sendErrorResponse(exchange, 401, "Invalid TPO credentials");
            return;
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("role", "TPO");
        resp.put("token", "dummy-tpo-token-" + officer.getId());
        resp.put("user", officer);
        HttpUtil.sendSuccessResponse(exchange, 200, "TPO Login successful", resp);
    }

    private void handleRegister(HttpExchange exchange) throws IOException, SQLException {
        String body = HttpUtil.readRequestBody(exchange);
        Student student = HttpUtil.getGson().fromJson(body, Student.class);

        if (student.getEmail() == null || student.getFullName() == null || student.getRollNo() == null) {
            HttpUtil.sendErrorResponse(exchange, 400, "Full Name, Email, and Roll Number are required");
            return;
        }

        Student existing = studentDao.findByEmail(student.getEmail());
        if (existing != null) {
            HttpUtil.sendErrorResponse(exchange, 409, "Email is already registered");
            return;
        }

        Student created = studentDao.register(student);
        Map<String, Object> resp = new HashMap<>();
        resp.put("role", "STUDENT");
        resp.put("user", created);
        HttpUtil.sendSuccessResponse(exchange, 201, "Registration successful", resp);
    }
}
