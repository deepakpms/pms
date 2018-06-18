package com.cvvid.models.candidate;

import java.io.Serializable;

public class ProfilesViewsModel implements Serializable {

    private static final long serialVersionUID = 1L;

    String view_count;
    String forenames;
    String surname;
    String current_job;
    String location;
    String id;

    public ProfilesViewsModel() {
    }

    public ProfilesViewsModel(String id, String forenames, String surname, String current_job, String location, String view_count) {
        this.id = id;
        this.view_count = view_count;
        this.forenames = forenames;
        this.surname = surname;
        this.current_job = current_job;
        this.location = location;
    }

    public String getView_count() {
        return view_count;
    }

    public void setView_count(String view_count) {
        this.view_count = view_count;
    }

    public String getForenames() {
        return forenames;
    }

    public void setForenames(String forenames) {
        this.forenames = forenames;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getCurrent_job() {
        return current_job;
    }

    public void setCurrent_job(String current_job) {
        this.current_job = current_job;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
