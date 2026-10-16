package com.campushire;

import com.campushire.config.DatabaseConfig;
import com.campushire.controller.*;
import com.campushire.util.HttpUtil;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class CampusHireApplication {
    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {}
        } else if (System.getenv("PORT") != null) {
            try {
                port = Integer.parseInt(System.getenv("PORT"));
            } catch (NumberFormatException ignored) {}
        }

        System.out.println("=================================================");
        System.out.println("  🎓 CampusHire™ - Smarter Campus Placements   ");
        System.out.println("  Java Backend & MySQL DBMS Enterprise Server   ");
        System.out.println("=================================================");

        // 1. Verify MySQL connection
        System.out.println("[Bootstrap] Verifying MySQL connection...");
        if (DatabaseConfig.testConnection()) {
            System.out.println("[Bootstrap] ✅ Successfully connected to MySQL database: campushire_db");
        } else {
            System.err.println("[Bootstrap] ⚠️ Warning: Could not connect to MySQL! Please ensure MySQL80 service is running.");
        }

        // 2. Start HTTP Server
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);

            // API Controllers
            AuthController authController = new AuthController();
            DriveController driveController = new DriveController();
            StudentController studentController = new StudentController();
            ApplicationController applicationController = new ApplicationController();
            TpoController tpoController = new TpoController();

            server.createContext("/api/auth", authController);
            server.createContext("/api/drives", driveController);
            server.createContext("/api/students", studentController);
            server.createContext("/api/applications", applicationController);
            server.createContext("/api/stats", tpoController);
            server.createContext("/api/announcements", tpoController);
            server.createContext("/api/interviews", tpoController);
            server.createContext("/api/export", tpoController);

            // Health endpoint
            server.createContext("/api/health", exchange -> {
                if (HttpUtil.handlePreflight(exchange)) return;
                HttpUtil.sendSuccessResponse(exchange, 200, "CampusHire Backend is healthy", System.currentTimeMillis());
            });

            // Static files from workspace root
            File rootDir = new File(".").getCanonicalFile();
            server.createContext("/", new StaticFileHandler(rootDir.getAbsolutePath()));

            // Multi-threaded executor
            server.setExecutor(Executors.newFixedThreadPool(16));
            server.start();

            System.out.println("[Bootstrap] 🚀 Server running at: http://localhost:" + port);
            System.out.println("[Bootstrap] 🌐 Frontend available at: http://localhost:" + port + "/");
            System.out.println("[Bootstrap] 📡 REST API Base: http://localhost:" + port + "/api");
            System.out.println("=================================================");

        } catch (IOException e) {
            System.err.println("[Bootstrap] Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
