package com.example.client2.controller;

import com.example.client2.model.Notification;
import com.example.client2.model.Token;
import com.example.client2.repo.TokenRepo;
import com.example.client2.service.AndroidPushNotificationsService;
import com.example.client2.service.AndroidPushPeriodicNotifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

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

    final AndroidPushNotificationsService androidPushNotificationsService;

    public NotificationController(TokenRepo tokenRepo, AndroidPushNotificationsService androidPushNotificationsService) {
        this.tokenRepo = tokenRepo;
        this.androidPushNotificationsService = androidPushNotificationsService;
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    ResponseEntity<Map<String, String>> sample() {
        ResponseEntity<Map<String, String>> response = null;

        Map<String, String> resMap = new HashMap<String, String>();
        resMap.put("type", "Second eureka client!");
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

    public ResponseEntity<String> sending(HttpEntity request) {
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

