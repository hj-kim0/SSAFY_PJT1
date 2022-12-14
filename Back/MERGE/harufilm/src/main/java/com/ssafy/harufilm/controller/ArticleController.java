package com.ssafy.harufilm.controller;

import java.util.ArrayList;
import java.util.List;

import com.ssafy.harufilm.dto.account.SmallProfileResponseDto;
import com.ssafy.harufilm.entity.User;
import com.ssafy.harufilm.fcm.FcmController;
import com.ssafy.harufilm.service.subscribe.SubscribeService;
import com.ssafy.harufilm.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.harufilm.common.ErrorResponseBody;
import com.ssafy.harufilm.common.MessageBody;
import com.ssafy.harufilm.dto.article.ArticleDetailRequestDto;
import com.ssafy.harufilm.dto.article.ArticleDetailResponseDto;
import com.ssafy.harufilm.dto.article.ArticleLikeyStatusRequestDto;
import com.ssafy.harufilm.dto.article.ArticleRequestDto;
import com.ssafy.harufilm.dto.article.ArticleShareRequestDto;
import com.ssafy.harufilm.dto.article.ArticleShowRequestDto;
import com.ssafy.harufilm.dto.article.ArticleShowSubResponseDto;
import com.ssafy.harufilm.entity.Article;
import com.ssafy.harufilm.service.article.ArticleService;

@RestController
@RequestMapping("/api/article")
public class ArticleController {

    @Autowired
    ArticleService articleService;

    @Autowired
    SubscribeService subscribeService;

    @Autowired
    UserService userService;

    @PostMapping("/create")
    public ResponseEntity<?> setArticle(@Validated ArticleRequestDto articleRequestDto) throws Exception {

        try {
            String imgstring = articleRequestDto.getImgdata().get(0).getOriginalFilename();
            String videostring = articleRequestDto.getVideodata().get(0).getOriginalFilename();

            if (imgstring.equals("") || videostring.equals(""))
                return ResponseEntity.status(500).body(ErrorResponseBody.of(500, false,
                        "????????? or ????????? ????????? ????????????. T^T"));

            articleService.articleSave(articleRequestDto);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(ErrorResponseBody.of(500, false,
                    "Internal Server Error, ??? ?????? ??????"));
        }

        // ??? ?????? ??????
        User articleUser = userService.getuserbyPid(articleRequestDto.getUserpid());
        // ??? ?????? ????????? ????????????
        List<SmallProfileResponseDto> followerList = subscribeService.followerList(articleRequestDto.getUserpid());

        for (int i = 0; i < followerList.size(); i++) {
            User follower = userService.getuserbyPid(followerList.get(i).getUserpid());

            FcmController.FCMMessaging(follower.getUserfcmtoken(), "????????? ?????? ??? ??? ??????", articleUser.getUsername() + "?????? ??? ?????? ?????????????????????");
        }

        return ResponseEntity.status(200).body(MessageBody.of(true, "??? ?????? ??????"));
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteArticle(@RequestBody ArticleShareRequestDto articleShareRequestDto) {
        try {
            articleService.articleDelete(articleShareRequestDto.getArticleidx());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ErrorResponseBody.of(500, false,
                    "Internal Server Error, ??? ?????? ??????"));
        }
        return ResponseEntity.status(200).body(MessageBody.of(true, "??? ?????? ??????"));
    }

    @PostMapping("/sharecontrol")
    public ResponseEntity<?> sharecontrol(@RequestBody ArticleShareRequestDto articleShareRequestDto) {
        try {
            articleService.SetShare(articleShareRequestDto);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(ErrorResponseBody.of(500, false,
                    "Internal Server Error,?????? ?????? ??????"));
            // TODO: handle exception
        }
        return ResponseEntity.status(200).body(MessageBody.of(true, "?????? ?????? ??????"));
    }

    @PostMapping("/showarticle") // ?????? article?????? ?????? ???????????? ??????
    public ResponseEntity<?> showarticle(@RequestBody ArticleShowRequestDto articleShowRequestDto) {

        List<Article> articles;
        List<ArticleDetailResponseDto> list = new ArrayList<>();
        try {
            articles = articleService.getArticle(articleShowRequestDto);
            for (int i = 0; i < articles.size(); i++) {
                ArticleDetailResponseDto temp = new ArticleDetailResponseDto();
                temp.setArticle(articles.get(i));
                temp.setLikey(articleService.getLikey(articles.get(i).getArticleidx()));
                temp.setHash(articleService.getHash(articles.get(i).getArticleidx()));
                list.add(temp);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ErrorResponseBody.of(500, false,
                    "Internal Server Error, ??? ?????? ???????????? ??????"));
        }
        return ResponseEntity.status(200).body(list);
    }

    @PostMapping("/showsubarticle")
    public ResponseEntity<?> list(@RequestBody ArticleRequestDto articleRequestDto) {
        List<Article> articles;
        List<ArticleDetailResponseDto> list = new ArrayList<>();
        Boolean check = false;
        try {

            articles = articleService.getFollowedArticleList(articleRequestDto.getUserpid());
            if(articles.size() == 0)
            {
                articles = articleService.recommendArticleList(articleRequestDto.getUserpid());
                check = true;
            }
            // System.out.println(articles.size());
            
            for (int i = 0; i < articles.size(); i++) {
                ArticleDetailResponseDto temp = new ArticleDetailResponseDto();
                temp.setArticle(articles.get(i));
                temp.setLikey(articleService.getLikey(articles.get(i).getArticleidx()));
                temp.setHash(articleService.getHash(articles.get(i).getArticleidx()));
                list.add(temp);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ErrorResponseBody.of(500, false,
                    "Internal Server Error, ??? ?????? ???????????? ??????"));
        }
        return ResponseEntity.status(200).body(ArticleShowSubResponseDto.of(list, check));
    }

    @PostMapping("/getarticle")
    public ResponseEntity<?> getArticle(@RequestBody ArticleDetailRequestDto articleDetailRequestDto) {
        Article article;
        int likey;
        List<String> hash = new ArrayList<>();
        try {
            article = articleService.findByArticleidx(articleDetailRequestDto.getArticleidx());
            likey = articleService.getLikey(articleDetailRequestDto.getArticleidx());
            hash = articleService.getHash(articleDetailRequestDto.getArticleidx());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ErrorResponseBody.of(500, false,
                    "Internal Server Error, ??? ???????????? ??????"));
        }
        return ResponseEntity.status(200).body(ArticleDetailResponseDto.of(article, likey, hash));
    }

    @PostMapping("/likey")
    public ResponseEntity<?> getArticle(@RequestBody ArticleLikeyStatusRequestDto articleLikeyStatusRequestDto) {
        boolean likeystatus;
        try {
            likeystatus = articleService.getLikeystatus(articleLikeyStatusRequestDto.getUserpid(),
                    articleLikeyStatusRequestDto.getArticleidx());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ErrorResponseBody.of(500, false, "????????? ?????? ???????????? ??????"));
        }
        return ResponseEntity.status(200).body(MessageBody.of(likeystatus, "????????? ?????? ??????"));
    }
}
