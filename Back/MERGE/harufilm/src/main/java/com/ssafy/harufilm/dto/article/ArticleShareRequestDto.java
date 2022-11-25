package com.ssafy.harufilm.dto.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ArticleShareRequestDto {
    private int articleidx;
    private int sharenum;
}
