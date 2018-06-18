package com.cvvid.models.employer;

import java.io.Serializable;

public class QuestionsModel implements Serializable {

    private String id;
    private String questions;

    public QuestionsModel() {
    }

    public QuestionsModel(String id, String questions) {
        this.id = id;
        this.questions = questions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }
}
