package com.campushire.model;

import java.sql.Timestamp;

public class Application {
    private int id;
    private int studentId;
    private int driveId;
    private String studentName;
    private String studentRollNo;
    private String studentDepartment;
    private double studentCgpa;
    private String companyName;
    private String roleTitle;
    private String location;
    private String packageCtc;
    private String logoAbbr;
    private String colorAccent;
    private Timestamp appliedDate;
    private String currentStage;
    private String stageLabel;
    private String roundsCompleted;
    private String nextStepDetails;
    private String offerPackage;
    private boolean isOfferAccepted;

    public Application() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getDriveId() { return driveId; }
    public void setDriveId(int driveId) { this.driveId = driveId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentRollNo() { return studentRollNo; }
    public void setStudentRollNo(String studentRollNo) { this.studentRollNo = studentRollNo; }

    public String getStudentDepartment() { return studentDepartment; }
    public void setStudentDepartment(String studentDepartment) { this.studentDepartment = studentDepartment; }

    public double getStudentCgpa() { return studentCgpa; }
    public void setStudentCgpa(double studentCgpa) { this.studentCgpa = studentCgpa; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getRoleTitle() { return roleTitle; }
    public void setRoleTitle(String roleTitle) { this.roleTitle = roleTitle; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getPackageCtc() { return packageCtc; }
    public void setPackageCtc(String packageCtc) { this.packageCtc = packageCtc; }

    public String getLogoAbbr() { return logoAbbr; }
    public void setLogoAbbr(String logoAbbr) { this.logoAbbr = logoAbbr; }

    public String getColorAccent() { return colorAccent; }
    public void setColorAccent(String colorAccent) { this.colorAccent = colorAccent; }

    public Timestamp getAppliedDate() { return appliedDate; }
    public void setAppliedDate(Timestamp appliedDate) { this.appliedDate = appliedDate; }

    public String getCurrentStage() { return currentStage; }
    public void setCurrentStage(String currentStage) { this.currentStage = currentStage; }

    public String getStageLabel() { return stageLabel; }
    public void setStageLabel(String stageLabel) { this.stageLabel = stageLabel; }

    public String getRoundsCompleted() { return roundsCompleted; }
    public void setRoundsCompleted(String roundsCompleted) { this.roundsCompleted = roundsCompleted; }

    public String getNextStepDetails() { return nextStepDetails; }
    public void setNextStepDetails(String nextStepDetails) { this.nextStepDetails = nextStepDetails; }

    public String getOfferPackage() { return offerPackage; }
    public void setOfferPackage(String offerPackage) { this.offerPackage = offerPackage; }

    public boolean isOfferAccepted() { return isOfferAccepted; }
    public void setOfferAccepted(boolean offerAccepted) { isOfferAccepted = offerAccepted; }
}
