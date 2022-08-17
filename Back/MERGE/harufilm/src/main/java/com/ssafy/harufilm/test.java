package com.ssafy.harufilm;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class test {

    @GetMapping("/ssss")
    public String func() {
        return "testclear";
    }
}