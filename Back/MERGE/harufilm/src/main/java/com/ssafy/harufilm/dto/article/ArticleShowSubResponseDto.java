package com.ssafy.harufilm.dto.article;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleShowSubResponseDto {

    private List<ArticleDetailResponseDto> list;

    private Boolean check;

    public static ArticleShowSubResponseDto of(List<ArticleDetailResponseDto> list,Boolean check)
    {
        ArticleShowSubResponseDto body = new ArticleShowSubResponseDto();
        body.setList(list);
        body.setCheck(check);

        return body;
    }
    
}
