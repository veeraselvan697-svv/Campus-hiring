-- CampusHire Placement Engine Database Schema
CREATE DATABASE IF NOT EXISTS campushire_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE campushire_db;

-- 1. TPO Officers / Admins
CREATE TABLE IF NOT EXISTS tpo_officers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    title VARCHAR(100) NOT NULL DEFAULT 'Head of Training & Placement (TPO)',
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Students
CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    roll_no VARCHAR(50) UNIQUE NOT NULL,
    degree VARCHAR(50) DEFAULT 'B.Tech',
    department VARCHAR(50) DEFAULT 'CSE',
    batch_year INT DEFAULT 2025,
    cgpa DECIMAL(3,2) NOT NULL DEFAULT 8.50,
    skills TEXT,
    resume_url VARCHAR(255) DEFAULT 'rahul_verma_resume.pdf',
    verification_status VARCHAR(20) DEFAULT 'VERIFIED',
    profile_score INT DEFAULT 85,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Companies
CREATE TABLE IF NOT EXISTS companies (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    logo_abbr VARCHAR(10) NOT NULL,
    color_accent VARCHAR(20) DEFAULT '#0052CC',
    website VARCHAR(255),
    description TEXT
);

-- 4. Placement Drives
CREATE TABLE IF NOT EXISTS drives (
    id INT AUTO_INCREMENT PRIMARY KEY,
    company_id INT NOT NULL,
    role_title VARCHAR(100) NOT NULL,
    location VARCHAR(100) NOT NULL DEFAULT 'India',
    package_ctc VARCHAR(50) NOT NULL,
    package_num DECIMAL(5,2) DEFAULT 8.0,
    min_cgpa DECIMAL(3,2) NOT NULL DEFAULT 7.00,
    eligible_branches VARCHAR(255) DEFAULT 'CSE, IT, ECE',
    required_skills VARCHAR(255) NOT NULL,
    deadline_days VARCHAR(50) DEFAULT '5 Days Left',
    status VARCHAR(20) DEFAULT 'ACTIVE',
    openings INT DEFAULT 25,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE
);

-- 5. Applications
CREATE TABLE IF NOT EXISTS applications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    drive_id INT NOT NULL,
    applied_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    current_stage VARCHAR(50) DEFAULT 'APPLIED',
    stage_label VARCHAR(50) DEFAULT 'In Review',
    rounds_completed VARCHAR(50) DEFAULT '1/3 Complete',
    next_step_details VARCHAR(255) DEFAULT 'Awaiting resume screening review',
    offer_package VARCHAR(50) DEFAULT NULL,
    is_offer_accepted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (drive_id) REFERENCES drives(id) ON DELETE CASCADE,
    UNIQUE KEY uq_student_drive (student_id, drive_id)
);

-- 6. Interviews
CREATE TABLE IF NOT EXISTS interviews (
    id INT AUTO_INCREMENT PRIMARY KEY,
    application_id INT NOT NULL,
    round_name VARCHAR(100) NOT NULL,
    scheduled_time VARCHAR(100) NOT NULL,
    meeting_link VARCHAR(255) DEFAULT 'https://meet.campushire.io/room-tech',
    status VARCHAR(20) DEFAULT 'SCHEDULED',
    FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE
);

-- 7. Announcements / Activity Logs
CREATE TABLE IF NOT EXISTS announcements (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    target_branch VARCHAR(50) DEFAULT 'ALL',
    badge VARCHAR(50) DEFAULT 'NEW DRIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- SEED DATA
-- Default TPO Officer
INSERT IGNORE INTO tpo_officers (id, name, title, email, password) VALUES 
(1, 'Dr. K. Ramanathan', 'Head of Training & Placement (TPO)', 'tpo@college.edu', 'admin123');

-- Default Demo Students (Matching PDF: Rahul Verma & Priya Sundaram)
INSERT IGNORE INTO students (id, full_name, email, password, roll_no, degree, department, batch_year, cgpa, skills, verification_status, profile_score) VALUES 
(1, 'Rahul Verma', 'rahul.verma@college.edu', 'student123', '21CS084', 'B.Tech', 'CSE', 2025, 8.70, 'Java, SQL, Spring Boot, Problem Solving, React', 'VERIFIED', 85),
(2, 'Priya Sundaram', 'priya.sundaram@college.edu', 'student123', '21CS102', 'B.Tech', 'CSE', 2025, 9.10, 'Java, DSA, Cloud, Microservices, Python', 'VERIFIED', 95),
(3, 'Ankit Sharma', 'ankit.sharma@college.edu', 'student123', '21IT045', 'B.Tech', 'IT', 2025, 7.30, 'Python, Web Dev, SQL, Git', 'PENDING', 70);

-- Companies from the project PDF
INSERT IGNORE INTO companies (id, name, category, logo_abbr, color_accent, website, description) VALUES 
(1, 'Zoho Corporation', 'SaaS Leader', 'Z', '#E5252A', 'https://www.zoho.com', 'Global enterprise SaaS and business software suite.'),
(2, 'Infosys Limited', 'Global MNC', 'INF', '#007CC3', 'https://www.infosys.com', 'Next-generation digital services and consulting leader.'),
(3, 'Tata Consultancy Services', 'Day-1 Recruiter', 'TCS', '#1B365D', 'https://www.tcs.com', 'IT services, consulting, and business solutions multinational.'),
(4, 'Accenture', 'Enterprise', 'ACN', '#A100FF', 'https://www.accenture.com', 'Global professional services company with leading capabilities in digital, cloud, and security.'),
(5, 'Wipro', 'Digital Tech', 'WIP', '#FF6600', 'https://www.wipro.com', 'Technology services and consulting company focused on building innovative solutions.'),
(6, 'Cognizant', 'GenC Partner', 'CTS', '#1F51FF', 'https://www.cognizant.com', 'Leading modern IT services company engineering modern businesses.');

-- Live Campus Drives (Page 2, 3, 4 of PDF)
INSERT IGNORE INTO drives (id, company_id, role_title, location, package_ctc, package_num, min_cgpa, eligible_branches, required_skills, deadline_days, status, openings) VALUES 
(1, 1, 'Software Developer', 'Chennai, India', '₹8–12 LPA', 10.00, 7.50, 'CSE, IT, ECE', 'Java, SQL, Problem Solving', '4 Days Left', 'ACTIVE', 35),
(2, 2, 'System Engineer', 'Bengaluru, India', '₹6–8 LPA', 7.00, 7.00, 'CSE, IT, ECE, MECH', 'Python, OOP, Web Dev', '6 Days Left', 'ACTIVE', 80),
(3, 3, 'Graduate Engineer Trainee', 'Hyderabad, India', '₹7.0 LPA', 7.00, 7.00, 'CSE, IT, ECE, EEE', 'Java, DSA, Cloud', '1 Week Left', 'ACTIVE', 120),
(4, 4, 'Associate Software Engineer', 'Pune, India', '₹9–11 LPA', 10.00, 7.80, 'CSE, IT', 'C++, Spring Boot, Database', '10 Days Left', 'ACTIVE', 25),
(5, 5, 'Project Engineer (Turbo)', 'Bengaluru, India', '₹6.5–8.5 LPA', 7.50, 7.20, 'CSE, IT, ECE', 'Java, Spring, REST API', '12 Days Left', 'ACTIVE', 45),
(6, 6, 'GenC Next Developer', 'Chennai, India', '₹6.75–9 LPA', 8.00, 7.50, 'CSE, IT', 'Full Stack, Cloud, Algorithms', '15 Days Left', 'ACTIVE', 50);

-- Applications for Rahul Verma (id = 1) as depicted in page 1 & 6 of PDF:
-- 1. Zoho: Software Developer (Shortlisted, Rounds 2/3 complete)
-- 2. Infosys: System Engineer (In Review, Aptitude Cleared)
-- 3. TCS: Graduate Engineer (Interview, Tech Round Ready)
INSERT IGNORE INTO applications (id, student_id, drive_id, current_stage, stage_label, rounds_completed, next_step_details, offer_package, is_offer_accepted) VALUES 
(1, 1, 1, 'SHORTLISTED', 'Shortlisted', '2/3 Complete', 'Technical Interview (Round 2) scheduled tomorrow at 10:00 AM', '₹10.5 LPA', FALSE),
(2, 1, 2, 'APTITUDE_CLEARED', 'In Review', '1/3 Complete', 'Aptitude cleared. Technical evaluation under review by recruiters.', NULL, FALSE),
(3, 1, 3, 'TECH_INTERVIEW', 'Interview', '2/3 Ready', 'Tech Round Ready. Interview scheduled for Friday, 2:30 PM.', NULL, FALSE);

-- Interviews for Rahul Verma
INSERT IGNORE INTO interviews (id, application_id, round_name, scheduled_time, meeting_link, status) VALUES 
(1, 1, 'Zoho Technical (Tomorrow)', 'Tomorrow at 10:00 AM', 'https://meet.campushire.io/zoho-tech-21cs084', 'SCHEDULED'),
(2, 3, 'TCS Technical Round 1', 'Friday at 2:30 PM', 'https://meet.campushire.io/tcs-eval-21cs084', 'SCHEDULED'),
(3, 1, 'Zoho Culture & HR Fit', 'Next Monday at 11:30 AM', 'https://meet.campushire.io/zoho-hr-21cs084', 'SCHEDULED');

-- Announcements matching PDF banner: "3 new drives opened for CSE branch today"
INSERT IGNORE INTO announcements (id, title, content, target_branch, badge) VALUES 
(1, '3 new drives opened for CSE branch today', 'Zoho Corp, Infosys Limited, and Tata Consultancy Services have posted new roles with ₹7–12 LPA CTC.', 'CSE', 'ACTIVE DRIVE'),
(2, 'Resume Verification Deadline Notice', 'All final year 2025 batch candidates must get their CGPA verified by TPO desk before Friday.', 'ALL', 'IMPORTANT'),
(3, 'Day-1 Recruiter Slot Confirmed: TCS & Zoho', 'Slotting schedules released for online assessment tests starting this weekend.', 'ALL', 'ANNOUNCEMENT');
