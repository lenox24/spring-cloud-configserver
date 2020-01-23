package com.example.client1.model;

import com.google.gson.annotations.SerializedName;

public class Topic {
    @SerializedName("title")
    String title;

    @SerializedName("body")
    String body;

    @SerializedName("topic")
    String topic;

    public Topic() {
        this.title = null;
        this.body = null;
        this.topic = null;
    }

    public Topic(String title, String body, String topic) {
        this.title = title;
        this.body = body;
        this.topic = topic;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
