package com.cvvid.models.candidate;

import java.io.Serializable;

public class LanguageModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String proficiency;

    public LanguageModel() {
    }

    public LanguageModel(String id, String name, String proficiency) {
        this.id = id;
        this.name = name;
        this.proficiency = proficiency;
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

    public String getProficiency() {
        return proficiency;
    }

    public void setProficiency(String proficiency) {
        this.proficiency = proficiency;
    }
}
