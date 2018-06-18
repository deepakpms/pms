package com.cvvid.models.candidate;

import java.io.Serializable;

public class PExperienceModel implements Serializable {

    private static final long serialVersionUID = 1L;

    String company_name;
    String location;
    String position;
    String start_date;
    String end_date;
    String description;
    String id;

    public PExperienceModel() {
    }

    public PExperienceModel(String id, String company_name, String location, String position, String start_date, String end_date, String description) {
        this.id = id;
        this.company_name = company_name;
        this.location = location;
        this.position = position;
        this.start_date = start_date;
        this.end_date = end_date;
        this.description = description;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
