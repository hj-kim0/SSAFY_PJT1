package com.ssafy.harufilm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.harufilm.common.MessageBody;
import com.ssafy.harufilm.dto.account.SigndownRequestDto;
import com.ssafy.harufilm.entity.User;
import com.ssafy.harufilm.service.user.UserService;
import com.ssafy.harufilm.common.ErrorResponseBody;

@RestController
@RequestMapping("/api/account")
public class SigndownController {

    @Autowired
    private PasswordEncoder passwordencoder;

    @Autowired
    private UserService userService;


    @PostMapping("/signdown")
    public ResponseEntity<?> signdown(@RequestBody SigndownRequestDto signdownRequestDto) {
        User user;
        try {
            user = userService.getuserbyPid(signdownRequestDto.getUserpid());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ErrorResponseBody.of(500, false,
                    "Internal Server Error 회원 탈퇴 실패 ^O^"));
        }

        if (!passwordencoder.matches(signdownRequestDto.getUserpassword(), user.getUserpassword())) {
            return ResponseEntity.status(400).body(ErrorResponseBody.of(400, false, "비밀번호가 잘못 되었습니다. ^0^"));
        } else {

            try {
                userService.signdown(user);
                return ResponseEntity.status(200).body(MessageBody.of(true, "회원 탈퇴가 완료 되었습니다. 잘가라 배.신.자"));
            } catch (Exception e) {
                return ResponseEntity.status(500).body(ErrorResponseBody.of(500, false,
                        "Internal Server Error 회원 탈퇴 실패 ^O^"));
            }
        }
    }
}
