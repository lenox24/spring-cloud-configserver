package com.example.client1.model;

import com.google.gson.annotations.SerializedName;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class Token {
    @Id
    ObjectId _id;

    @SerializedName("token")
    private String token;

    @SerializedName("type")
    private String type = "android";

    @SerializedName("agree")
    private String agree;

    public Token() {
        this._id = null;
        this.token = null;
        this.type = null;
        this.agree = null;
    }

    public void setToken(String token, String type, String agree) {
        this.token = token;
        this.type = type;
        this.agree = agree;
    }

    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAgree() {
        return agree;
    }

    public void setAgree(String agree) {
        this.agree = agree;
    }
}
