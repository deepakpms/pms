package com.cvvid.models.candidate;

import java.io.Serializable;

public class EducationModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String institution;
    private String field;
    private String completion_date;
    private String description;
    private String level;

    public EducationModel() {
    }

    public EducationModel(String id, String institution, String field, String completion_date, String description, String level) {
        this.id = id;
        this.institution = institution;
        this.field = field;
        this.completion_date = completion_date;
        this.description = description;
        this.level = level;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getCompletion_date() {
        return completion_date;
    }

    public void setCompletion_date(String completion_date) {
        this.completion_date = completion_date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
