package com.ssafy.harufilm.fcm;

import org.springframework.http.HttpEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

public class NotificationService {
    private static final String SERVER_KEY = "AAAAz80bO58:APA91bG4vUfEncUUiDU845j0KzNd3VnPXbHLiKhAlsGIGaGG9WHAhVlZJyIbgfbMuqca-09-mHKBsMzvI5XvfOA3vx1oQeveNbme1B2qjc-1SX2gQC8xKKGDUHUdoi9BEutiEgEGlMAg";
    private static final String API_URL = "https://fcm.googleapis.com/fcm/send";

    public static void send(HttpEntity<String> entity) {
        RestTemplate restTemplate = new RestTemplate();

        ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new HeaderRequestInterceptor("Authorization", "key=" + SERVER_KEY));
        interceptors.add(new HeaderRequestInterceptor("Content-Type", "application/json; charset=utf-8"));
        restTemplate.setInterceptors(interceptors);

        restTemplate.postForObject(API_URL, entity, String.class);
    }
}
