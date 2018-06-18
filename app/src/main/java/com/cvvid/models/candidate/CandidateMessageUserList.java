package com.cvvid.models.candidate;

import java.io.Serializable;

public class CandidateMessageUserList implements Serializable {

    private static final long serialVersionUID = 1L;

    private String conversation_id;
    private String user_id;
    private String name;
    private String created_at;
    private String subject;
    private String message;

    public CandidateMessageUserList() {
    }

    public CandidateMessageUserList(String conversation_id, String user_id, String name,String subject,String message, String created_at) {
        this.conversation_id = conversation_id;
        this.user_id = user_id;
        this.name = name;
        this.subject = subject;
        this.message = message;
        this.created_at = created_at;
    }

    public String getConversation_id() {
        return conversation_id;
    }

    public void setConversation_id(String conversation_id) {
        this.conversation_id = conversation_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
