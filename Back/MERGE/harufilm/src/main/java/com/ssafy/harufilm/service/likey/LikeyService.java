package com.ssafy.harufilm.service.likey;

import com.ssafy.harufilm.dto.likey.LikeyRequestDto;
import com.ssafy.harufilm.entity.Likey;

public interface LikeyService {
    Likey likeySave(LikeyRequestDto likeyRequestDto); // 새 좋아요 저장
    void likeyDelete(LikeyRequestDto likeyRequestDto);// 좋아요 삭제
}
