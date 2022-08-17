package com.ssafy.harufilm.service.subscribe;

import java.util.List;

import com.ssafy.harufilm.dto.account.SmallProfileResponseDto;
import com.ssafy.harufilm.dto.subscribe.SubscribeRequestDto;
import com.ssafy.harufilm.entity.Subscribe;

public interface SubscribeService{
    Subscribe subscribeSave(SubscribeRequestDto subscribeRequestDto); // 새 팔로우 저장
    void subscribeDelete(SubscribeRequestDto subscribeRequestDto);// 팔로우 삭제

    List<SmallProfileResponseDto> followerList(int userpid);//userpid를 구독한 유저들~팔로워의 스몰프로필 리스트
    List<SmallProfileResponseDto> followList(int userpid);//내가 구독한 유저들~팔로우한 유저들의 스몰프로필 리스트

    Boolean followBoolean(int userpid, int targetpid);
}
