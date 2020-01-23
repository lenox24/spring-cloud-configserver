package com.example.client1.controller;

import com.example.client1.model.Notification;
import com.example.client1.model.Token;
import com.example.client1.model.Topic;
import com.example.client1.repo.TokenRepo;
import com.example.client1.service.AndroidPushNotificationsService;
import com.example.client1.service.AndroidPushPeriodicNotifications;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@CrossOrigin
@RestController
@EnableDiscoveryClient
public class NotificationController {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private final TokenRepo tokenRepo;
    private static final String firebase_server_key = "AAAAMo_6ug8:APA91bGdWvGlnFFNYmd1VCmWwJe_-6aA_oU566w2TQyCvcd8UL8IGC5RuV65ha8VcTA5h9ZOTK7n6BMXfktkCnrs2Mo3EQ3rrKNA2dFJeTNE8xavX19Oeh-u-f2PkNzLZspxlfK4aNoY";

    final AndroidPushNotificationsService androidPushNotificationsService;

    public NotificationController(TokenRepo tokenRepo, AndroidPushNotificationsService androidPushNotificationsService) {
        this.tokenRepo = tokenRepo;
        this.androidPushNotificationsService = androidPushNotificationsService;
    }

    @PostMapping("/topic/notification")
    public ResponseEntity<String> topicNotification(@RequestBody Topic reqJson) throws FirebaseMessagingException {

        RestTemplate template = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "key=" + firebase_server_key);

        JSONObject json = new JSONObject();
        String topic = "/topics/" + reqJson.getTopic();

        json.put("to", topic);

        JSONObject data = new JSONObject();
        data.put("title", reqJson.getTitle());
        data.put("body", reqJson.getBody());

        json.put("data", data);

        HttpEntity<String> entity = new HttpEntity<>(json.toJSONString(), headers);

        return template.exchange("https://fcm.googleapis.com/fcm/send", HttpMethod.POST, entity, String.class);
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    ResponseEntity<Map<String, String>> sample() {
        ResponseEntity<Map<String, String>> response = null;

        Map<String, String> resMap = new HashMap<String, String>();
        resMap.put("type", "First eureka client!");
        resMap.put("message", "Spring Cloud is awesome!");

        response = new ResponseEntity<Map<String, String>>(resMap, HttpStatus.OK);

        return response;
    }

    @PostMapping("/update/agree")
    @ResponseBody
    public ResponseEntity updateAgree(@RequestBody Token reqJson) {
        System.out.println(reqJson.toString());
        Token model = tokenRepo.findByToken(reqJson.getToken());
        if (model == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        model.setAgree(reqJson.getAgree());
        tokenRepo.save(model);

        return new ResponseEntity(HttpStatus.OK);
    }

    @Scheduled(fixedRate = 10000)
    @PostMapping("/notification")
    @ResponseBody
    public ResponseEntity<String> notificationAll(@RequestBody Notification reqJson) {
        String title = reqJson.getTitle();
        String body = reqJson.getBody();

        String notifications = AndroidPushPeriodicNotifications.PeriodicNotificationJson(tokenRepo.findByAgree("true"), title, body);

        HttpEntity<String> request = new HttpEntity<>(notifications);

        return sending(request);
    }

    @Scheduled(fixedRate = 10000)
    @PostMapping("/notification/type")
    @ResponseBody
    public ResponseEntity<String> notificationType(@RequestBody Notification reqJson) {
        String type = reqJson.getType();
        String title = reqJson.getTitle();
        String body = reqJson.getBody();

        String notifications = AndroidPushPeriodicNotifications.PeriodicNotificationJson(tokenRepo.findByTypeAndAgree(type, "true"), title, body);

        HttpEntity<String> request = new HttpEntity<>(notifications);

        return sending(request);
    }

    @PostMapping("/send/token")
    @ResponseBody
    public ResponseEntity sendToken(@RequestBody Token reqJson) {
        System.out.println(reqJson.toString());
        String token = reqJson.getToken();
        String type = reqJson.getType();
        String agree = reqJson.getAgree();

        Token model = new Token();
        model.setToken(token, type, agree);
        tokenRepo.insert(model);

        return new ResponseEntity(HttpStatus.OK);
    }

    public ResponseEntity<String> sending(HttpEntity<String> request) {
        CompletableFuture<String> pushNotification = androidPushNotificationsService.send(request);
        CompletableFuture.allOf(pushNotification).join();

        try {
            String firebaseResponse = pushNotification.get();

            return new ResponseEntity<>(firebaseResponse, HttpStatus.OK);
        } catch (InterruptedException e) {
            logger.debug("got interrupted!");

            e.printStackTrace();
        } catch (ExecutionException e) {
            logger.debug("execution error!");

            e.printStackTrace();
        }

        return new ResponseEntity<>("Push Notification ERROR!", HttpStatus.BAD_REQUEST);
    }
}

