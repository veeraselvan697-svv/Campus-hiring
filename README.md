# 🎓 CampusHire™ — Smarter Campus Placements Platform
> **Enterprise Full-Stack Placement Engine**  
> Built with **HTML5 / CSS3 / Vanilla JavaScript Frontend**, **Java 17 REST Backend**, and **MySQL 8.0 Relational DBMS**.

---

## 🌟 Overview
**CampusHire** is an institutional campus placement management platform connecting students, visiting recruiters, and Training & Placement Officers (TPO). It streamlines every milestone—from credential verification and personalized opportunity matching to 1-click applications, real-time interview scheduling, and automated offer letter management.

This platform faithfully implements the designs and specifications from `project.pdf`.

---

## 🛠️ Technology Stack

| Layer | Technology | Details |
|---|---|---|
| **Frontend** | HTML5, Modern CSS3, JavaScript (ES6+) | Glassmorphism, Google Fonts (*Plus Jakarta Sans*, *Outfit*), CSS Grid/Flexbox, fully responsive, zero external CSS framework dependencies |
| **Backend** | Java 17 (Amazon Corretto LTS) | Standalone Java HTTP REST Server (`com.sun.net.httpserver`), Multi-threaded worker pool, Native JDBC, Google Gson for JSON processing |
| **DBMS** | MySQL 8.0 Server | Normalized relational database (`campushire_db`) with 7 tables, foreign key constraints, indexes, and full seed data |
| **APIs** | RESTful JSON Endpoints | Dual CORS-enabled API for public and authenticated administrative endpoints |

---

## 🚀 Quick Start Guide

### 1. Prerequisites
- **MySQL Server 8.0** running locally on port `3306` (Credentials: `root` / `root`).
- Portable **JDK 17** is already included in the `tools/jdk-17/` directory.

### 2. Start the Java Server
Double-click `start-server.bat` in the project root, or execute via PowerShell / CMD:
```powershell
.\start-server.bat
```
The server will automatically compile all Java source files and launch the HTTP server on port **8080**:
- **Web Application URL:** `http://localhost:8080/`
- **REST API Base URL:** `http://localhost:8080/api`

---

## 🗄️ Database Architecture (`campushire_db`)

The database schema is defined in `backend/schema.sql` and includes:
1. `students` — Manages candidate profiles, roll numbers, degrees, CGPA scores, technical skill tags, resume URLs, and TPO verification statuses.
2. `companies` — Enterprise hiring partners (e.g., Zoho, Infosys, TCS, Accenture, Wipro, Cognizant) with categories, branding accents, and logos.
3. `drives` — Active and upcoming placement opportunities with package CTC, cut-off CGPAs, target branches, and application windows.
4. `applications` — Tracks student applications across stages (`APPLIED`, `APTITUDE_CLEARED`, `SHORTLISTED`, `TECH_INTERVIEW`, `OFFER_EXTENDED`, `OFFER_ACCEPTED`).
5. `interviews` — Scheduled live interview rounds, meeting room links, and candidate slots.
6. `tpo_officers` — TPO administrators with credentials for institutional operations.
7. `announcements` — Campus circulars, Day-1 notifications, and live drive broadcasts.

To re-import or reset seed data:
```bash
mysql -u root -proot < backend/schema.sql
```

---

## 💻 Key Features & Workflow

### 1. Student Portal (e.g. *Rahul Verma*, CGPA 8.7)
- **Live Hero Showcase:** Displays real-time candidate status, CGPA 8.7, active drive badge, and urgent branch alerts.
- **Personalized Eligibility Engine:** The system compares the student's CGPA against live recruiter cut-offs. Eligible drives show **1-Click Apply**, while ineligible drives explain why.
- **Live Application Tracking:** Click **"View Rounds"** to see round-by-round progression (Online Aptitude Cleared ➔ Tech Round 2 ➔ HR Interview ➔ Offer Letter).
- **Offer Letter Acceptance:** Once an offer is extended (e.g., Zoho ₹10.5 LPA), students can formally accept the placement offer directly in the portal.

### 2. TPO Operations Deck (e.g. *Dr. K. Ramanathan*)
- **Central Operations Dashboard:** Real-time KPI deck showing 1,240 students, 58 companies, 112 drives, 3,420 applications, and 482 offers issued (87.4% target achieved).
- **➕ Schedule New Drive:** TPO can publish new corporate recruitment drives with custom CTC, minimum CGPA, and branch eligibility directly to MySQL.
- **📥 Instant CSV Export:** One-click download of the complete NAAC/NIRF eligibility list for any drive in official CSV format.
- **🛡️ Student Verification:** TPO desk can review pending student transcripts and certify academic marks with one click.
- **📢 Broadcast Announcements:** Broadcast flash alerts (e.g. Day-1 slot confirmations) to student dashboards.

### 3. Interactive Persona Switcher
Use the role selector pill in the top header to instantly switch between:
- **Rahul Verma (Student):** Experience 1-click apply, round timelines, and offer acceptance.
- **Dr. K. Ramanathan (TPO Head):** Test administrative deck, drive scheduling, and CSV audits.
- **Guest Visitor:** Explore open drives and campus testimonials.

---

## 📡 REST API Reference

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/health` | Backend and DB connectivity status check |
| `GET` | `/api/stats` | Aggregated institutional placement statistics |
| `GET` | `/api/drives` | Fetch all drives with joined recruiter metadata |
| `POST` | `/api/drives` | Schedule and publish a new placement drive |
| `GET` | `/api/applications?studentId=1` | Get active applications for a student |
| `POST` | `/api/applications` | Submit 1-click application (enforces CGPA cut-off) |
| `PUT` | `/api/applications/{id}/stage` | Advance applicant milestone (Aptitude, Tech, Offer) |
| `POST` | `/api/applications/{id}/accept-offer`| Formally accept placement offer |
| `GET` | `/api/export/eligibility?driveId=1` | Download official CSV list of eligible candidates |
| `POST` | `/api/auth/student/login` | Student authentication endpoint |
| `POST` | `/api/auth/tpo/login` | TPO administrator authentication endpoint |

---

## 📂 Project Structure
```
c:\Users\User\Desktop\website\
├── index.html                   # Complete semantic, accessible web interface
├── css/
│   └── styles.css               # Modern design system, responsive styles, animations
├── js/
│   ├── api.js                   # Frontend API client communicating with Java server
│   └── app.js                   # UI controller, state management, modals, and workflows
├── assets/
│   └── images/
│       ├── logo.jpg             # High-res vector CampusHire mortarboard + rocket logo
│       └── hero_banner.jpg      # Futuristic university tech placement campus illustration
├── backend/
│   ├── schema.sql               # MySQL database schema and seed data
│   ├── bin/                     # Compiled Java bytecode (.class files)
│   ├── lib/
│   │   ├── mysql-connector-j-8.3.0.jar  # JDBC MySQL driver
│   │   └── gson-2.10.1.jar      # Google Gson JSON library
│   └── src/com/campushire/
│       ├── CampusHireApplication.java    # Main Java server bootstrap
│       ├── config/DatabaseConfig.java    # JDBC connection manager
│       ├── controller/                   # REST HTTP handlers & static file server
│       ├── dao/                          # Data Access Objects (JDBC queries)
│       ├── model/                        # Normalized entity POJOs
│       └── util/HttpUtil.java            # CORS & JSON HTTP utilities
├── tools/
│   └── jdk-17/                  # Self-contained portable Java 17 LTS JDK
├── start-server.bat             # One-click Windows runner to compile & launch
└── project.pdf                  # Original design wireframe & requirements document
```
