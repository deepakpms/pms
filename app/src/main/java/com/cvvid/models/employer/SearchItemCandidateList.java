package com.cvvid.models.employer;

public class SearchItemCandidateList {

    public SearchItemCandidateList() {
    }

    public SearchItemCandidateList(String id, String name, String skills, String location, String job, String image) {
        this.id = id;
        this.name = name;
        this.skills = skills;
        this.location = location;
        this.job = job;
        this.image = image;
    }

    private String id;
    private String name;
    private String skills;
    private String location;
    private String job;
    private String image;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
