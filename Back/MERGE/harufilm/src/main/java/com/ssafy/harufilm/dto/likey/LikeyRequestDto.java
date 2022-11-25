package com.ssafy.harufilm.dto.likey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeyRequestDto {
    
    private int likeyfrom;

    private int likeyto;
}
