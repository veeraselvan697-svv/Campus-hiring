package com.campushire.model;

import java.sql.Timestamp;

public class Drive {
    private int id;
    private int companyId;
    private String companyName;
    private String companyCategory;
    private String companyLogoAbbr;
    private String companyColorAccent;
    private String roleTitle;
    private String location;
    private String packageCtc;
    private double packageNum;
    private double minCgpa;
    private String eligibleBranches;
    private String requiredSkills;
    private String deadlineDays;
    private String status;
    private int openings;
    private int applicantCount;
    private Timestamp createdAt;

    public Drive() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getCompanyCategory() { return companyCategory; }
    public void setCompanyCategory(String companyCategory) { this.companyCategory = companyCategory; }

    public String getCompanyLogoAbbr() { return companyLogoAbbr; }
    public void setCompanyLogoAbbr(String companyLogoAbbr) { this.companyLogoAbbr = companyLogoAbbr; }

    public String getCompanyColorAccent() { return companyColorAccent; }
    public void setColorAccent(String colorAccent) { this.companyColorAccent = colorAccent; }

    public String getRoleTitle() { return roleTitle; }
    public void setRoleTitle(String roleTitle) { this.roleTitle = roleTitle; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getPackageCtc() { return packageCtc; }
    public void setPackageCtc(String packageCtc) { this.packageCtc = packageCtc; }

    public double getPackageNum() { return packageNum; }
    public void setPackageNum(double packageNum) { this.packageNum = packageNum; }

    public double getMinCgpa() { return minCgpa; }
    public void setMinCgpa(double minCgpa) { this.minCgpa = minCgpa; }

    public String getEligibleBranches() { return eligibleBranches; }
    public void setEligibleBranches(String eligibleBranches) { this.eligibleBranches = eligibleBranches; }

    public String getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(String requiredSkills) { this.requiredSkills = requiredSkills; }

    public String getDeadlineDays() { return deadlineDays; }
    public void setDeadlineDays(String deadlineDays) { this.deadlineDays = deadlineDays; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getOpenings() { return openings; }
    public void setOpenings(int openings) { this.openings = openings; }

    public int getApplicantCount() { return applicantCount; }
    public void setApplicantCount(int applicantCount) { this.applicantCount = applicantCount; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
