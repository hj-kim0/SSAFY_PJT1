package com.ssafy.harufilm.dto.account;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SigninResponseDto {

    private int userpid;

    private String token;

    private int todayarticleidx;

    private String message;

    public static SigninResponseDto of(String message, Integer userpid, String token, int todayarticleidx) {
        SigninResponseDto body = new SigninResponseDto();
        body.setUserpid(userpid);
        body.setToken(token);
        body.setMessage(message);
        body.setTodayarticleidx(todayarticleidx);
        return body;
    }
}
