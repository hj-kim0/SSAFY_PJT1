package com.ssafy.harufilm.service.likey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ssafy.harufilm.dto.likey.LikeyRequestDto;
import com.ssafy.harufilm.entity.Likey;
import com.ssafy.harufilm.repository.likey.LikeyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikeyServiceImpl implements LikeyService{

    @Autowired
    LikeyRepository likeyRepository;

    @Override
    public Likey likeySave(LikeyRequestDto likeyRequestDto) {
        Likey likey = Likey.builder()
                .likeyfrom(likeyRequestDto.getLikeyfrom())
                .likeyto(likeyRequestDto.getLikeyto())
                .build();

        return likeyRepository.save(likey);
    }

    @Override
    public void likeyDelete(LikeyRequestDto likeyRequestDto) {
        Likey likey = likeyRepository.findByLikeyfromAndLikeyto(likeyRequestDto.getLikeyfrom(),likeyRequestDto.getLikeyto()).orElse(null);
        if(likey!=null){
            likeyRepository.delete(likey);
        }
    }


    
}
