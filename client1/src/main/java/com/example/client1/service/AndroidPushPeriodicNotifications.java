package com.example.client1.service;

import com.example.client1.model.Token;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;


public class AndroidPushPeriodicNotifications {
    public static String PeriodicNotificationJson(List<Token> tokenList, String title, String body) {
        JSONArray array = new JSONArray();

        for (Token token : tokenList) {
            array.put(token.getToken());
        }

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("registration_ids", array);

        JSONObject notification = new JSONObject();
        notification.put("title", title);
        notification.put("body", body);

        jsonBody.put("notification", notification);

        System.out.println(jsonBody.toString());

        return jsonBody.toString();
    }
}
