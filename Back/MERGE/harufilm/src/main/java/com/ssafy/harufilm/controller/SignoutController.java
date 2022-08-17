package com.ssafy.harufilm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.harufilm.common.ErrorResponseBody;
import com.ssafy.harufilm.common.MessageBody;
import com.ssafy.harufilm.dto.account.SignoutRequestDto;
import com.ssafy.harufilm.service.user.UserService;

@RestController
@RequestMapping("/api/account")
public class SignoutController {
    
    @Autowired
    UserService userService;

    @PostMapping("/signout")
    public ResponseEntity<?> signout(@RequestBody SignoutRequestDto signoutRequestDto)
    {
        //DB Token 삭제
        try{
        userService.deletefcmtoken(signoutRequestDto.getUserpid());
        }
        catch(Exception e){
            return ResponseEntity.status(500).body(ErrorResponseBody.of(500, false,
                    "Internal Server Error 로그아웃 실패"));
        }
        
        return ResponseEntity.status(200).body(MessageBody.of(true, "로그아웃이 완료되었습니다. ^0^"));
    }

}
