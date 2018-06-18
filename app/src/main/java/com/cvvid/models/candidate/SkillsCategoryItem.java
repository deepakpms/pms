package com.cvvid.models.candidate;

public class SkillsCategoryItem {

    private String id, name;

    public SkillsCategoryItem()
    {

    }

    public SkillsCategoryItem(String id, String name) {
        this.id = id;
        this.name = name;
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
}
