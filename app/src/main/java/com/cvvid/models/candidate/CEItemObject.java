package com.cvvid.models.candidate;

import org.json.JSONArray;

import java.util.ArrayList;

public class CEItemObject {

    public CEItemObject(String id, String name, boolean status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public String id;
    public String name;
    public Boolean status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
