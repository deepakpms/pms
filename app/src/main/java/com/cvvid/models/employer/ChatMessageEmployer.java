package com.cvvid.models.employer;


public class ChatMessageEmployer {

   public String id;
   public String conversation_id;
   public String user_id;
   public String name;
   public String message;
   public String created_at;



    public ChatMessageEmployer()
    {

    }

    public ChatMessageEmployer(String id, String conversation_id, String user_id, String name, String message, String created_at) {
        super();
        this.id = id;
        this.conversation_id = conversation_id;
        this.user_id = user_id;
        this.message = message;
        this.name = name;
        this.created_at = created_at;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
