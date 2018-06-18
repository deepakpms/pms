package com.cvvid.models.employer;

import java.util.ArrayList;

public class ApplicationGroupItemsInfo {

    private String id,name;
    private ArrayList<ApplicationChildItem> list = new ArrayList<ApplicationChildItem>();

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

    public ArrayList<ApplicationChildItem> getList() {
        return list;
    }

    public void setList(ArrayList<ApplicationChildItem> list) {
        this.list = list;
    }
}
