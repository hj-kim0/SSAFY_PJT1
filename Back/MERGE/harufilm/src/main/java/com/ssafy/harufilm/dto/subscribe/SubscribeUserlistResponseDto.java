package com.ssafy.harufilm.dto.subscribe;

import java.util.List;

import com.ssafy.harufilm.dto.account.SmallProfileResponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscribeUserlistResponseDto {
    List<SmallProfileResponseDto> list;
    public static SubscribeUserlistResponseDto of(List<SmallProfileResponseDto> list){
        SubscribeUserlistResponseDto body = new SubscribeUserlistResponseDto();
        body.setList(list);
        return body;
    }
}
