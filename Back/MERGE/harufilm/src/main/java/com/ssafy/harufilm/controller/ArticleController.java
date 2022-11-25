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
                        "이미지 or 비디오 파일이 없습니다. T^T"));

            articleService.articleSave(articleRequestDto);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(ErrorResponseBody.of(500, false,
                    "Internal Server Error, 글 저장 실패"));
        }

        // 글 작성 유저
        User articleUser = userService.getuserbyPid(articleRequestDto.getUserpid());
        // 글 작성 유저의 팔로워들
        List<SmallProfileResponseDto> followerList = subscribeService.followerList(articleRequestDto.getUserpid());

        for (int i = 0; i < followerList.size(); i++) {
            User follower = userService.getuserbyPid(followerList.get(i).getUserpid());

            FcmController.FCMMessaging(follower.getUserfcmtoken(), "팔로우 유저 새 글 알림", articleUser.getUsername() + "님이 새 글을 작성하셨습니다");
        }

        return ResponseEntity.status(200).body(MessageBody.of(true, "글 저장 성공"));
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteArticle(@RequestBody ArticleShareRequestDto articleShareRequestDto) {
        try {
            articleService.articleDelete(articleShareRequestDto.getArticleidx());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ErrorResponseBody.of(500, false,
                    "Internal Server Error, 글 삭제 실패"));
        }
        return ResponseEntity.status(200).body(MessageBody.of(true, "글 삭제 성공"));
    }

    @PostMapping("/sharecontrol")
    public ResponseEntity<?> sharecontrol(@RequestBody ArticleShareRequestDto articleShareRequestDto) {
        try {
            articleService.SetShare(articleShareRequestDto);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(ErrorResponseBody.of(500, false,
                    "Internal Server Error,공유 설정 실패"));
            // TODO: handle exception
        }
        return ResponseEntity.status(200).body(MessageBody.of(true, "공유 설정 완료"));
    }

    @PostMapping("/showarticle") // 일단 article정보 모두 가져오는 걸로
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
                    "Internal Server Error, 글 목록 불러오기 실패"));
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
                    "Internal Server Error, 글 목록 불러오기 실패"));
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
                    "Internal Server Error, 글 불러오기 실패"));
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
            return ResponseEntity.status(500).body(ErrorResponseBody.of(500, false, "좋아요 여부 가져오기 실패"));
        }
        return ResponseEntity.status(200).body(MessageBody.of(likeystatus, "좋아요 여부 출력"));
    }
}
