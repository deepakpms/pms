package com.cvvid.models.employer;

import java.io.Serializable;

public class AnswersModel implements Serializable {

    private String id;
    private String username;
    private String questions;
    private String videourl;

    public AnswersModel() {
    }

    public AnswersModel(String id, String username, String questions, String videourl) {
        this.id = id;
        this.username = username;
        this.questions = questions;
        this.videourl = videourl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    public String getVideourl() {
        return videourl;
    }

    public void setVideourl(String videourl) {
        this.videourl = videourl;
    }
}
