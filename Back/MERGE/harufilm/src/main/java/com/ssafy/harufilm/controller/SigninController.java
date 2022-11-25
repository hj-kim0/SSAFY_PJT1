package com.ssafy.harufilm.controller;

import com.ssafy.harufilm.fcm.FcmController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.harufilm.common.ErrorResponseBody;
import com.ssafy.harufilm.dto.account.SigninRequestDto;
import com.ssafy.harufilm.dto.account.SigninResponseDto;
import com.ssafy.harufilm.entity.User;
import com.ssafy.harufilm.jwt.JwtTokenProvider;
import com.ssafy.harufilm.service.article.ArticleService;
import com.ssafy.harufilm.service.user.UserService;

@RestController
@RequestMapping("/api/account")
public class SigninController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ArticleService articleService;

    @PostMapping("/signin")
    public ResponseEntity<?> userCheckAndSendToken(@RequestBody SigninRequestDto signinRequestDto) throws Exception {
        User user = new User();

        try {
            user = userService.getuserbyId(signinRequestDto.getUserid());

        } catch (Exception e) {

            return ResponseEntity.status(500).body(ErrorResponseBody.of(500, false,
                    "Internal Server Error,로그인 실패"));
        }
        if (user == null) {
            return ResponseEntity.status(400).body(ErrorResponseBody.of(400, false, "아이디 또는 비밀번호가 입력이 잘못 되었습니다."));

        }

        if (!passwordEncoder.matches(signinRequestDto.getUserpassword(), user.getUserpassword())) {
            return ResponseEntity.status(400).body(ErrorResponseBody.of(400, false, "아이디 또는 비밀번호가 입력이 잘못 되었습니다."));
        }
        String token = jwtTokenProvider.createToken((signinRequestDto.getUserid()));

        userService.setuserfcmtoken(user.getUserpid(), signinRequestDto.getUserfcmtoken());

        int todayarticleidx = articleService.getTodayarticle(user.getUserpid());

        List<String> messagelist = new ArrayList<String>();
        messagelist.add("방가방가~ ✧*｡٩(ˊᗜˋ*)و✧*｡");
        messagelist.add("이랏샤이마세!!! (＞Д＜)ゝ");
        messagelist.add("HELLO~ (。◕ ∀ ◕｡)");
        messagelist.add("혼저옵서◝(⁰▿⁰)◜");
        Random random = new Random();
        int idx = random.nextInt(messagelist.size() - 1);

        FcmController.FCMMessaging(signinRequestDto.getUserfcmtoken(), "로그인 완료! ^0^", messagelist.get(idx));
        return ResponseEntity.status(200).body(SigninResponseDto.of("로그인 완료", user.getUserpid(), token,
                todayarticleidx));

    }
}
