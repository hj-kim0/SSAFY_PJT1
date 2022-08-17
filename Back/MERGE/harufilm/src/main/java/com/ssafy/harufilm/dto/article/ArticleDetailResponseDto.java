package com.ssafy.harufilm.dto.article;

import java.util.List;

import com.ssafy.harufilm.entity.Article;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleDetailResponseDto {
    Article article;
    int likey;
    List<String> hash;

    public static ArticleDetailResponseDto of(Article article, int likey, List<String> hash){
        ArticleDetailResponseDto body = new ArticleDetailResponseDto();
        body.setArticle(article);
        body.setLikey(likey);
        body.setHash(hash);
        return body;
    }
}
