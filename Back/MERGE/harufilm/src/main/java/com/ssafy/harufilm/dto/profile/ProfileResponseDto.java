package com.ssafy.harufilm.dto.profile;

import com.ssafy.harufilm.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ProfileResponseDto {

    private String userid;

    private String username;

    // private String userpassword;

    private String userimg;

    private String userdesc;

    private String message;

    public static ProfileResponseDto of(String message, User user) {
        ProfileResponseDto body = new ProfileResponseDto();

        body.setMessage(message);
        body.setUserdesc(user.getUserdesc());
        body.setUserid(user.getUserid());
        body.setUserimg(user.getUserimg());
        body.setUsername(user.getUsername());

        return body;
    }
}
