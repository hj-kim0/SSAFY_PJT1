package com.ssafy.harufilm.dto.account;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSearchResponseDto {

    private List<SmallProfileResponseDto> userlist;

    public static UserSearchResponseDto of(List<SmallProfileResponseDto> userlist) {
        UserSearchResponseDto body = new UserSearchResponseDto();
        body.setUserlist(userlist);
        return body;
    }
}
