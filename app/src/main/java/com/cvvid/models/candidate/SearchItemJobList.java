package com.cvvid.models.candidate;

public class SearchItemJobList {

    public SearchItemJobList() {
    }

    public SearchItemJobList(String id, String title, String salary, String location, String created_at, String description, String image) {
        this.id = id;
        this.title = title;
        this.salary = salary;
        this.location = location;
        this.created_at = created_at;
        this.description = description;
        this.image = image;
    }

    private String id;
    private String title;
    private String salary;
    private String location;
    private String created_at;
    private String description;
    private String image;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
