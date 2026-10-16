package com.campushire.model;

import java.sql.Timestamp;

public class Announcement {
    private int id;
    private String title;
    private String content;
    private String targetBranch;
    private String badge;
    private Timestamp createdAt;

    public Announcement() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getTargetBranch() { return targetBranch; }
    public void setTargetBranch(String targetBranch) { this.targetBranch = targetBranch; }

    public String getBadge() { return badge; }
    public void setBadge(String badge) { this.badge = badge; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
