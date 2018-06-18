package com.cvvid.models.candidate;

import java.io.Serializable;

public class HobbiesModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String activity;
    private String description;

    public HobbiesModel() {
    }

    public HobbiesModel(String id, String activity, String description) {
        this.id = id;
        this.activity = activity;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
