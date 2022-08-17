package com.ssafy.harufilm.dto.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleLikeyStatusRequestDto {
    
    int userpid;
    int articleidx;
}
