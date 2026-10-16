package com.campushire.controller;

import com.campushire.dao.DriveDao;
import com.campushire.model.Drive;
import com.campushire.util.HttpUtil;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DriveController implements HttpHandler {
    private final DriveDao driveDao = new DriveDao();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (HttpUtil.handlePreflight(exchange)) return;

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {
            if ("GET".equalsIgnoreCase(method)) {
                Map<String, String> params = HttpUtil.parseQueryParams(exchange);
                boolean activeOnly = "true".equalsIgnoreCase(params.get("active"));
                List<Drive> drives = driveDao.findAll(activeOnly);
                HttpUtil.sendJsonResponse(exchange, 200, drives);
            } else if ("POST".equalsIgnoreCase(method)) {
                String body = HttpUtil.readRequestBody(exchange);
                Drive d = HttpUtil.getGson().fromJson(body, Drive.class);
                if (d.getCompanyId() <= 0 || d.getRoleTitle() == null || d.getPackageCtc() == null) {
                    HttpUtil.sendErrorResponse(exchange, 400, "Missing required drive fields (companyId, roleTitle, packageCtc)");
                    return;
                }
                Drive created = driveDao.createDrive(d);
                HttpUtil.sendSuccessResponse(exchange, 201, "Drive scheduled successfully", created);
            } else if ("PUT".equalsIgnoreCase(method) && path.contains("/status")) {
                String[] segments = path.split("/");
                int id = Integer.parseInt(segments[3]);
                String body = HttpUtil.readRequestBody(exchange);
                JsonObject json = HttpUtil.getGson().fromJson(body, JsonObject.class);
                String status = json.has("status") ? json.get("status").getAsString() : "CLOSED";
                boolean updated = driveDao.updateStatus(id, status);
                if (updated) {
                    HttpUtil.sendSuccessResponse(exchange, 200, "Drive status updated", null);
                } else {
                    HttpUtil.sendErrorResponse(exchange, 404, "Drive not found");
                }
            } else {
                HttpUtil.sendErrorResponse(exchange, 405, "Method not allowed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            HttpUtil.sendErrorResponse(exchange, 500, "Error processing drive request: " + e.getMessage());
        }
    }
}
