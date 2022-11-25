package com.ssafy.harufilm.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.harufilm.common.ErrorResponseBody;
import com.ssafy.harufilm.common.MessageBody;
import com.ssafy.harufilm.dto.account.SmallProfileResponseDto;
import com.ssafy.harufilm.dto.account.UserSearchResponseDto;
import com.ssafy.harufilm.dto.article.ArticleDetailResponseDto;
import com.ssafy.harufilm.dto.search.KeywordDto;
import com.ssafy.harufilm.entity.Article;
import com.ssafy.harufilm.service.article.ArticleService;
import com.ssafy.harufilm.service.user.UserService;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    // 해당 키워드를 포함한 userid의 사용자 목록(userid, username, userimg),
    // 해당 키워드를 포함한 hashname의 게시글 목록(userid, userimg, articleidx, articlethumbnail)

    @Autowired
    UserService userService;

    @Autowired
    ArticleService articleService;

    @PostMapping("/user")
    public ResponseEntity<?> searchUserByKeyword(@RequestBody KeywordDto keyword) {
        List<SmallProfileResponseDto> userlist;
        try {
            userlist = userService.getuserlistbykeyword(keyword.getKeyword());
        } catch (Exception e) {
            return ResponseEntity.status(200).body(MessageBody.of(true, "글 불러오기 성공"));

        }
        return ResponseEntity.status(200).body(UserSearchResponseDto.of(userlist));
    }

    @PostMapping("/hash")
    public ResponseEntity<?> searchArticleBysearchHashByKeyword(@RequestBody KeywordDto keyword) {
        List<Article> articles;
        List<ArticleDetailResponseDto> list = new ArrayList<>();
        try {
            articles = articleService.getarticlelistbykeyword(keyword);

            for (int i = 0; i < articles.size(); i++) {
                ArticleDetailResponseDto temp = new ArticleDetailResponseDto();
                temp.setArticle(articles.get(i));
                temp.setLikey(articleService.getLikey(articles.get(i).getArticleidx()));
                temp.setHash(articleService.getHash(articles.get(i).getArticleidx()));
                list.add(temp);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ErrorResponseBody.of(500, false,
                    "Internal Server Error, 글 목록 불러오기 실패"));
        }
        return ResponseEntity.status(200).body(list);
    }
}
