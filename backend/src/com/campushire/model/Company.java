package com.campushire.model;

public class Company {
    private int id;
    private String name;
    private String category;
    private String logoAbbr;
    private String colorAccent;
    private String website;
    private String description;

    public Company() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLogoAbbr() { return logoAbbr; }
    public void setLogoAbbr(String logoAbbr) { this.logoAbbr = logoAbbr; }

    public String getColorAccent() { return colorAccent; }
    public void setColorAccent(String colorAccent) { this.colorAccent = colorAccent; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
