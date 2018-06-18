package com.cvvid.models.employer;

import java.io.Serializable;

public class EmployerOfficeModel implements Serializable {

    private String id;
    private String location;
    private String status;

    public EmployerOfficeModel() {
    }

    public EmployerOfficeModel(String id, String location, String status) {
        this.id = id;
        this.location = location;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
