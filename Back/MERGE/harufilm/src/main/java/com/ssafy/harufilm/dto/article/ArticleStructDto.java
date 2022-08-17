package com.ssafy.harufilm.dto.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleStructDto {
    String userpid; // 아티클 작성 유저
    
    String articlethumbnail; //아티클 썸네일
}
