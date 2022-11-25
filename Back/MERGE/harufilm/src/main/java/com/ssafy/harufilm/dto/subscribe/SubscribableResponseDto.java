package com.ssafy.harufilm.dto.subscribe;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscribableResponseDto {
    
    boolean followBoolean;
    public static SubscribableResponseDto of(boolean followBoolean) {
        SubscribableResponseDto body = new SubscribableResponseDto();
        body.setFollowBoolean(followBoolean);
        return body;
    }
}
