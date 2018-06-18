package com.cvvid.models.candidate;

import java.io.Serializable;
import java.util.List;

public class MembershipItem implements Serializable {

    private String id;
    private String name;
    private String price;
    private List<Object> objectList;

    public MembershipItem()
    {

    }

    public MembershipItem(String id, String name, String price, List objectList) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.objectList = objectList;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public List<Object> getObjectList() {
        return objectList;
    }

    public void setObjectList(List<Object> objectList) {
        this.objectList = objectList;
    }
}
