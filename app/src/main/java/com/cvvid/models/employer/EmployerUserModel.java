package com.cvvid.models.employer;

import java.io.Serializable;

public class EmployerUserModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String email;
    private String postedjobs;
    public EmployerUserModel() {
    }
    public EmployerUserModel(String id, String name, String email,String postedjobs) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.postedjobs = postedjobs;
    }
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPostedjobs() {
        return postedjobs;
    }

    public void setPostedjobs(String postedjobs) {
        this.postedjobs = postedjobs;
    }
}
