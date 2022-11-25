package com.ssafy.harufilm.controller;

import java.io.IOException;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.harufilm.common.MessageBody;
import com.ssafy.harufilm.dto.profile.ModifyRequestDto;
import com.ssafy.harufilm.dto.profile.ProfileRequestDto;
import com.ssafy.harufilm.dto.profile.ProfileResponseDto;
import com.ssafy.harufilm.entity.User;
import com.ssafy.harufilm.service.user.UserService;

@RestController
@RequestMapping("/api/profile")

public class ProfileController {

    @Autowired
    private UserService userService;

    // 프로필 조회시 요청
    @PostMapping("/join")
    public ResponseEntity<?> getProfile(@RequestBody ProfileRequestDto profileRequestDto) throws SQLException {

        User user = new User();
        user = userService.getuserbyPid(profileRequestDto.getUserpid());

        return ResponseEntity.status(200).body(ProfileResponseDto.of("프로필 조회 성공", user));
    }

    @PostMapping("/modify")
    public ResponseEntity<?> setProfile(@Validated ModifyRequestDto modifyRequestDto)
            throws SQLException, IllegalStateException, IOException {

        userService.modifyprofile(modifyRequestDto);

        return ResponseEntity.status(200).body(MessageBody.of(true, "프로필 변경 완료"));
    }

}
