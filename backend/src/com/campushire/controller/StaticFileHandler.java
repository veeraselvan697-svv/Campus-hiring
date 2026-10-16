package com.campushire.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StaticFileHandler implements HttpHandler {
    private final String rootDir;

    public StaticFileHandler(String rootDir) {
        this.rootDir = rootDir;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String uriPath = exchange.getRequestURI().getPath();
        if (uriPath == null || uriPath.equals("/") || uriPath.isEmpty()) {
            uriPath = "/index.html";
        }

        // Prevent path traversal
        File requestedFile = new File(rootDir, uriPath.replace("/", File.separator)).getCanonicalFile();
        File baseDir = new File(rootDir).getCanonicalFile();

        if (!requestedFile.getPath().startsWith(baseDir.getPath())) {
            sendResponse(exchange, 403, "Access Denied".getBytes(), "text/plain");
            return;
        }

        if (requestedFile.isDirectory()) {
            requestedFile = new File(requestedFile, "index.html");
        }

        if (!requestedFile.exists()) {
            // Fallback to index.html for SPA-like routing if not an asset file
            if (!uriPath.contains(".")) {
                requestedFile = new File(rootDir, "index.html");
            }
            if (!requestedFile.exists()) {
                sendResponse(exchange, 404, "404 Not Found".getBytes(), "text/plain");
                return;
            }
        }

        String mimeType = getMimeType(requestedFile.getName());
        byte[] fileBytes = Files.readAllBytes(requestedFile.toPath());
        sendResponse(exchange, 200, fileBytes, mimeType);
    }

    private void sendResponse(HttpExchange exchange, int statusCode, byte[] bytes, String contentType) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String getMimeType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".html") || lower.endsWith(".htm")) return "text/html; charset=UTF-8";
        if (lower.endsWith(".css")) return "text/css; charset=UTF-8";
        if (lower.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (lower.endsWith(".json")) return "application/json; charset=UTF-8";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".svg")) return "image/svg+xml";
        if (lower.endsWith(".webp")) return "image/webp";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".pdf")) return "application/pdf";
        if (lower.endsWith(".woff2")) return "font/woff2";
        if (lower.endsWith(".woff")) return "font/woff";
        if (lower.endsWith(".ttf")) return "font/ttf";
        return "application/octet-stream";
    }
}
