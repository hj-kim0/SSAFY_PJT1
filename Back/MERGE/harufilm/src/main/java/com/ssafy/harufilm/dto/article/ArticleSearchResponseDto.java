package com.ssafy.harufilm.dto.article;

import java.util.List;

import com.ssafy.harufilm.entity.Article;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleSearchResponseDto {

    private List<Article> articlelist;

    public static ArticleSearchResponseDto of(List<Article> articlelist) {
        ArticleSearchResponseDto body = new ArticleSearchResponseDto();
        body.setArticlelist(articlelist);
        return body;
    }
}
