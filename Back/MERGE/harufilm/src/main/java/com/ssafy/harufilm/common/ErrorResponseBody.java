package com.ssafy.harufilm.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponseBody {

    String message = null;

    Integer status = null;

    boolean success = false;

    public ErrorResponseBody() {
    }

    public static ErrorResponseBody of(Integer statusCode, Boolean success, String message) {
        ErrorResponseBody body = new ErrorResponseBody();
        body.message = message;
        body.status = statusCode;
        body.success = success;
        return body;
    }
}
