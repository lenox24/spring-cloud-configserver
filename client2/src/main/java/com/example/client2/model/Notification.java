package com.example.client2.model;

import com.google.gson.annotations.SerializedName;

public class Notification {
    @SerializedName("title")
    String title;

    @SerializedName("body")
    String body;

    @SerializedName("type")
    String type;

    public Notification() {
        this.title = null;
        this.body = null;
        this.type = null;
    }

    public Notification(String title, String body, String type) {
        this.title = title;
        this.body = body;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
