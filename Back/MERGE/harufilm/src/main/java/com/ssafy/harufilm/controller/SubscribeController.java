package com.ssafy.harufilm.controller;

import java.util.ArrayList;
import java.util.List;

import com.ssafy.harufilm.fcm.FcmController;
import com.ssafy.harufilm.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.harufilm.common.ErrorResponseBody;
import com.ssafy.harufilm.common.MessageBody;
import com.ssafy.harufilm.dto.account.SmallProfileResponseDto;
import com.ssafy.harufilm.dto.subscribe.SubScribeDetailRequestDto;
import com.ssafy.harufilm.dto.subscribe.SubscribableDto;
import com.ssafy.harufilm.dto.subscribe.SubscribableResponseDto;
import com.ssafy.harufilm.dto.subscribe.SubscribeRequestDto;
import com.ssafy.harufilm.dto.subscribe.SubscribeUserlistResponseDto;
import com.ssafy.harufilm.service.subscribe.SubscribeService;

@RestController
@RequestMapping("/api/subscribe")
public class SubscribeController {

    @Autowired
    private SubscribeService subscribeService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<?> setsub(@RequestBody SubscribeRequestDto subscribeRequestDto) {
        try {
            subscribeService.subscribeSave(subscribeRequestDto);

            String toUser = userService.getuserbyPid(subscribeRequestDto.getSubto()).getUserfcmtoken();
            String fromUser = userService.getuserbyPid(subscribeRequestDto.getSubfrom()).getUsername();

            if (toUser != null) {
                FcmController.FCMMessaging(toUser, "팔로워가 늘었어요!", fromUser + "님이 팔로우를 하셨습니다");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ErrorResponseBody.of(500, false,
                    "Internal Server Error, 팔로우 실패"));
        }
        return ResponseEntity.status(200).body(MessageBody.of(true, "팔로우"));
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deletesub(@RequestBody SubscribeRequestDto subscribeRequestDto) {
        try {
            subscribeService.subscribeDelete(subscribeRequestDto);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ErrorResponseBody.of(500, false,
                    "Internal Server Error, 팔로우 취소 실패"));
        }
        return ResponseEntity.status(200).body(MessageBody.of(true, "팔로우 취소"));

    }

    @PostMapping("/following")
    public ResponseEntity<?> following(@RequestBody SubScribeDetailRequestDto subscribeDetailRequestDto) {
        List<SmallProfileResponseDto> list;
        try {
            list = subscribeService.followList(subscribeDetailRequestDto.getUserpid());
            if (list == null) {
                list = new ArrayList<SmallProfileResponseDto>();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ErrorResponseBody.of(500, false,
                    "Internal Server Error, 팔로우 리스트 불러오기 실패"));
        }
        return ResponseEntity.status(200).body(SubscribeUserlistResponseDto.of(list));
    }

    @PostMapping("/followed")
    public ResponseEntity<?> followed(@RequestBody SubScribeDetailRequestDto subscribeDetailRequestDto) {
        List<SmallProfileResponseDto> list;

        try {
            list = subscribeService.followerList(subscribeDetailRequestDto.getUserpid());
            if (list == null) {
                list = new ArrayList<SmallProfileResponseDto>();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ErrorResponseBody.of(500, false,
                    "Internal Server Error, 팔로워 리스트 불러오기 실패"));
        }
        return ResponseEntity.status(200).body(SubscribeUserlistResponseDto.of(list));
    }

    @PostMapping("/follow")
    public ResponseEntity<?> follow(@RequestBody SubscribableDto subscribableDto) {
        boolean followBoolean;
        try {
            followBoolean = subscribeService.followBoolean(subscribableDto.getSubfrom(), subscribableDto.getSubto());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ErrorResponseBody.of(500, false,
                    "Internal Server Error, 팔로잉 여부 불러오기 실패"));
        }
        return ResponseEntity.status(200).body(SubscribableResponseDto.of(followBoolean));
    }
}
