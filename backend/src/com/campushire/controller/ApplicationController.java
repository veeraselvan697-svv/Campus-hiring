package com.campushire.controller;

import com.campushire.dao.ApplicationDao;
import com.campushire.dao.DriveDao;
import com.campushire.dao.StudentDao;
import com.campushire.model.Application;
import com.campushire.model.Drive;
import com.campushire.model.Student;
import com.campushire.util.HttpUtil;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ApplicationController implements HttpHandler {
    private final ApplicationDao appDao = new ApplicationDao();
    private final StudentDao studentDao = new StudentDao();
    private final DriveDao driveDao = new DriveDao();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (HttpUtil.handlePreflight(exchange)) return;

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {
            if ("GET".equalsIgnoreCase(method)) {
                Map<String, String> query = HttpUtil.parseQueryParams(exchange);
                if (query.containsKey("studentId")) {
                    int studentId = Integer.parseInt(query.get("studentId"));
                    List<Application> apps = appDao.findByStudentId(studentId);
                    HttpUtil.sendJsonResponse(exchange, 200, apps);
                } else {
                    List<Application> apps = appDao.findAll();
                    HttpUtil.sendJsonResponse(exchange, 200, apps);
                }
            } else if ("POST".equalsIgnoreCase(method) && path.endsWith("/accept-offer")) {
                String[] segments = path.split("/");
                int id = Integer.parseInt(segments[3]);
                boolean accepted = appDao.acceptOffer(id);
                if (accepted) {
                    HttpUtil.sendSuccessResponse(exchange, 200, "Congratulations! Corporate placement offer accepted successfully.", null);
                } else {
                    HttpUtil.sendErrorResponse(exchange, 404, "Application not found");
                }
            } else if ("POST".equalsIgnoreCase(method)) {
                String body = HttpUtil.readRequestBody(exchange);
                JsonObject json = HttpUtil.getGson().fromJson(body, JsonObject.class);
                if (!json.has("studentId") || !json.has("driveId")) {
                    HttpUtil.sendErrorResponse(exchange, 400, "studentId and driveId are required");
                    return;
                }
                int studentId = json.get("studentId").getAsInt();
                int driveId = json.get("driveId").getAsInt();

                // Validate student eligibility against drive min CGPA
                Student student = studentDao.findById(studentId);
                Drive drive = driveDao.findById(driveId);

                if (student == null || drive == null) {
                    HttpUtil.sendErrorResponse(exchange, 404, "Student or Drive not found");
                    return;
                }

                if (student.getCgpa() < drive.getMinCgpa()) {
                    HttpUtil.sendErrorResponse(exchange, 403, String.format(
                            "Eligibility requirement not met: Min CGPA required is %.2f, but student CGPA is %.2f",
                            drive.getMinCgpa(), student.getCgpa()));
                    return;
                }

                boolean applied = appDao.apply(studentId, driveId);
                if (applied) {
                    HttpUtil.sendSuccessResponse(exchange, 201, "Successfully applied to " + drive.getCompanyName() + " for " + drive.getRoleTitle(), null);
                } else {
                    HttpUtil.sendErrorResponse(exchange, 409, "You have already applied for this campus placement drive.");
                }
            } else if ("PUT".equalsIgnoreCase(method) && path.contains("/stage")) {
                String[] segments = path.split("/");
                int id = Integer.parseInt(segments[3]);
                String body = HttpUtil.readRequestBody(exchange);
                JsonObject json = HttpUtil.getGson().fromJson(body, JsonObject.class);

                String stage = json.has("stage") ? json.get("stage").getAsString() : "SHORTLISTED";
                String stageLabel = json.has("stageLabel") ? json.get("stageLabel").getAsString() : "Shortlisted";
                String roundsCompleted = json.has("roundsCompleted") ? json.get("roundsCompleted").getAsString() : "2/3 Complete";
                String nextStepDetails = json.has("nextStepDetails") ? json.get("nextStepDetails").getAsString() : "Interview scheduled";
                String offerPackage = (json.has("offerPackage") && !json.get("offerPackage").isJsonNull()) ? json.get("offerPackage").getAsString() : null;

                boolean updated = appDao.updateStage(id, stage, stageLabel, roundsCompleted, nextStepDetails, offerPackage);
                if (updated) {
                    HttpUtil.sendSuccessResponse(exchange, 200, "Candidate milestone updated to " + stageLabel, null);
                } else {
                    HttpUtil.sendErrorResponse(exchange, 404, "Application not found");
                }
            } else {
                HttpUtil.sendErrorResponse(exchange, 405, "Method not allowed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            HttpUtil.sendErrorResponse(exchange, 500, "Application error: " + e.getMessage());
        }
    }
}
