package com.ssafy.harufilm.common;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class MessageBody {
    
    String message = null;

    boolean success;

    public static MessageBody of(Boolean success,String message){
        MessageBody res = new MessageBody();
        res.setMessage(message);
        res.setSuccess(success);
        return res;
    }
}
