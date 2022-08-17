package com.ssafy.harufilm.fcm;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class FcmController {
    public static void FCMMessaging(String to, String title, String message) throws Exception {

         JSONObject body = new JSONObject();

         body.put("to",to);
         body.put("priority","high");

         JSONObject notification = new JSONObject();

         notification.put("title",title);
         notification.put("body",message);

         JSONObject data = new JSONObject();

         data.put("title",title);
         data.put("message",message);

         body.put("notification",notification);
         body.put("data",data);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

         HttpEntity<String> request = new HttpEntity<>(body.toString(),headers);

         NotificationService.send(request);
     }
}
