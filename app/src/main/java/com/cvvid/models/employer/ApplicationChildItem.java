package com.cvvid.models.employer;

public class ApplicationChildItem {

    private String email;
    private String status;
    private String date;

    public ApplicationChildItem()
    {

    }

    public ApplicationChildItem(String email, String status, String date) {
        this.email = email;
        this.status = status;
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
