package com.ssafy.harufilm.dto.account;

import lombok.Getter;
import lombok.Setter;


//팔로우리스트, 유저 검색에서 사용자에게 보여질 수준의 정보
@Getter
@Setter
public class SmallProfileResponseDto { 

    private int userpid;

    private String username;

    private String userimg;

    private String userid;

    public static SmallProfileResponseDto of(Integer userpid, String username, String userimg, String userid) {
        SmallProfileResponseDto body = new SmallProfileResponseDto();
        body.setUserpid(userpid);
        body.setUsername(username);
        body.setUserimg(userimg);
        body.setUserid(userid);
        return body;
    }
}
