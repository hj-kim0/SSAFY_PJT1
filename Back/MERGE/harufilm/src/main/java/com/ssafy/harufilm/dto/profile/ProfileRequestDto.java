package com.ssafy.harufilm.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

// 프로필 조회시 필요한 데이터
public class ProfileRequestDto {
    private int userpid;
}
