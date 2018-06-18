package com.cvvid.models.candidate;

public class CandidateMembershipModel {

    private String id, item_name, item_price;

    public CandidateMembershipModel(String id, String item_name, String item_price) {
        this.id = id;
        this.item_name = item_name;
        this.item_price = item_price;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_price() {
        return item_price;
    }

    public void setItem_price(String item_price) {
        this.item_price = item_price;
    }
}
