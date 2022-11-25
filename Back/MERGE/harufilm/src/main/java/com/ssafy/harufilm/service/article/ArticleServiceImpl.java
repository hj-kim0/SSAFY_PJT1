package com.ssafy.harufilm.service.article;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.harufilm.dto.article.ArticleRequestDto;
import com.ssafy.harufilm.dto.article.ArticleShareRequestDto;
import com.ssafy.harufilm.dto.article.ArticleShowRequestDto;
import com.ssafy.harufilm.dto.search.KeywordDto;
import com.ssafy.harufilm.entity.Article;
import com.ssafy.harufilm.entity.Hash;
import com.ssafy.harufilm.entity.Hashtag;
import com.ssafy.harufilm.entity.Likey;
import com.ssafy.harufilm.entity.Subscribe;
import com.ssafy.harufilm.repository.article.ArticleRepository;
import com.ssafy.harufilm.repository.hash.HashRepository;
import com.ssafy.harufilm.repository.hashtag.HashtagRepository;
import com.ssafy.harufilm.repository.likey.LikeyRepository;
import com.ssafy.harufilm.repository.subscribe.SubscribeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    HashRepository hashRepository;

    @Autowired
    HashtagRepository hashtagRepository;

    @Autowired
    SubscribeRepository subscribeRepository;

    @Autowired
    LikeyRepository likeyRepository;

    @Override
    public Article articleSave(ArticleRequestDto articleRequestDto) throws IllegalStateException, IOException {

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date time = new Date();

        String now = format.format(time);

        // userpid폴더가 있는지 검사한다.
        String path = "/var/opt/upload/article/" + articleRequestDto.getUserpid();
        File folder = new File(path);

        if (!folder.exists()) {
            try {
                folder.mkdir();
            } catch (Exception e) {
                e.getStackTrace();
            }
        }

        // 해당userpid폴더에 오늘날짜가 있는지 검사한다.
        path = "/var/opt/upload/article/" + articleRequestDto.getUserpid() + "/" + now;
        folder = new File(path);

        if (!folder.exists()) {
            try {
                folder.mkdir();
            } catch (Exception e) {
                e.getStackTrace();
            }
        }

        // 해당 날짜에 파일을 저장한다.
        // System.out.println(originfile.getOriginalFilename());

        // 이미지 저장
        try {
            List<MultipartFile> imgfile = articleRequestDto.getImgdata();
            for (int i = 0; i < imgfile.size(); ++i) {

                MultipartFile originfile = imgfile.get(i);
                String filename = originfile.getOriginalFilename();
                int idx = -1;
                idx = filename.lastIndexOf(".");

                String ChangeFilename = Integer.toString(i + 1) + filename.substring(idx, filename.length());
                File UpdateFile = new File(path + "/" + ChangeFilename);

                originfile.transferTo(UpdateFile);
            }
        } catch (Exception e) {
            System.out.println("no Data");
            // TODO: handle exception
        }

        // 비디오 저장
        try {
            List<MultipartFile> videofile = articleRequestDto.getVideodata();

            for (int i = 0; i < videofile.size(); ++i) {

                MultipartFile originfile = videofile.get(i);

                String filename = originfile.getOriginalFilename();
                int idx = -1;
                idx = filename.lastIndexOf(".");

                String ChangeFilename = Integer.toString(i + 1) + filename.substring(idx, filename.length());
                File UpdateFile = new File(path + "/" + ChangeFilename);

                originfile.transferTo(UpdateFile);
            }

        } catch (Exception e) {
            System.out.println("no Data");
            // TODO: handle exception
        }

        Article article = Article.builder()
                .articlethumbnail(articleRequestDto.getArticlethumbnail())
                .articleshare(0)
                .userpid(articleRequestDto.getUserpid())
                .articlecreatedate(now)
                .build();

        Article savedArticle = articleRepository.save(article);

        String hashlist = articleRequestDto.getHashlist();
        String[] list = hashlist.split(",");
        List<String> alist = new ArrayList<>();

        for (int i = 0; i < list.length; i++) {
            alist.add(list[i]);
        }

        Iterator<String> iter = alist.iterator();
        while (iter.hasNext()) {
            String hashname = iter.next();
            Hash h;
            h = hashRepository.findByHashname(hashname).orElse(null);
            if (h == null) {
                h = Hash.builder()
                        .hashname(hashname)
                        .build();
                hashRepository.save(h);
            }
            Hashtag ht;
            ht = Hashtag.builder()
                    .hashidx(h.getHashidx())
                    .articleidx(savedArticle.getArticleidx())
                    .build();
            hashtagRepository.save(ht);
        }
        return savedArticle;
    }

    @Override
    public void articleDelete(int articleidx) {
        Article article = articleRepository.findByArticleidx(articleidx).orElse(null);

        if (article != null) {

            String path = "/var/opt/upload/article/" + article.getUserpid() + "/" +
                    article.getArticlecreatedate();
            File folder = new File(path);

            if (folder.exists()) {
                try {
                    FileUtils.cleanDirectory(folder);
                    folder.delete();
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
            // 해당 날짜 폴더 삭제

            try {
                hashtagRepository.deleteAllByArticleidx(article.getArticleidx()); // 해시태그리스트에서 삭제
            } catch (Exception e) {
                e.getStackTrace();
            }
            articleRepository.deleteById(articleidx);
            // 테이블 삭제
        }

    }

    @Override
    public List<Article> getArticle(ArticleShowRequestDto articleShowRequestDto) {
        List<Article> list = new ArrayList<Article>();

        // 본인의 게시글을 가져올 경우
        if (articleShowRequestDto.getSearch_userpid() == articleShowRequestDto.getUserpid()) {
            list = articleRepository.findAllByUserpid(articleShowRequestDto.getUserpid());
        } else // 다른 사람의 게시글일 경우
        {
            // 일단 다 가져옴
            list = articleRepository.findAllByUserpid(articleShowRequestDto.getSearch_userpid());

            // 비공개 요소 삭제
            for (Iterator<Article> it = list.iterator(); it.hasNext();) {
                Article value = it.next();
                if (value.getArticleshare() == 2)
                    it.remove();
            }

            // userpid 유저가 search_pid 유저를 구독 안했을 경우 1번 요소도 삭제
            Subscribe subscribe = subscribeRepository.findBySubfromAndSubto(articleShowRequestDto.getUserpid(),
                    articleShowRequestDto.getSearch_userpid());
            if (subscribe == null) {
                for (Iterator<Article> it = list.iterator(); it.hasNext();) {
                    Article value = it.next();
                    if (value.getArticleshare() == 1)
                        it.remove();
                }

            }
        }

        return list;
    }

    @Override
    public List<Article> getFollowedArticleList(int userpid) {

        List<Article> list = articleRepository.articleList(userpid);
        
        // 비공개 요소 삭제
        for (Iterator<Article> it = list.iterator(); it.hasNext();) {
            Article value = it.next();
            if (value.getArticleshare() == 2)
                it.remove();
        }
        return list;
        

    }

    @Override
    public List<Article> recommendArticleList(int userpid) {
                    //1. 모든 게시글 가져오기
                    List<Article> articles = articleRepository.findAll();

                    //1-1. 비공개 요소 and 내 게시글 삭제
                    for (Iterator<Article> it = articles.iterator(); it.hasNext();) {
                        Article value = it.next();
                         if (value.getArticleshare() == 2 || value.getUserpid() == userpid)
                            it.remove();
                     }
        
                    class INFO implements Comparable{
                        public int articleidx;
                        public Long likeynum;
        
                        public Long getLikeynum() {
                            return this.likeynum;
                        }
                        @Override
                        public int compareTo(Object o) {
                            INFO e = (INFO) o;
                            if(e.likeynum > this.likeynum) return 1;
                           else if(e.likeynum == this.likeynum)
                           {
                            if(e.articleidx < this.articleidx) return 1;
                            else return -1;
                           }
                           else return -1;
                            // TODO Auto-generated method stub
                          }
                    }
        
                    //2. List에 정리
                    List<INFO> articlelist = new ArrayList<INFO>();
                    for(int i=0;i<articles.size();++i)
                    {
                        INFO info = new INFO();
                        info.articleidx = articles.get(i).getArticleidx();
                        info.likeynum = likeyRepository.articleLikeyCount(info.articleidx);
                        articlelist.add(info);
                    }
        
                    //3. 정렬
                    Collections.sort(articlelist);
        
                    //4. 10개의 게시글만 보내기
                    List<Article> resultlist = new ArrayList<Article>();
        
                    if(articlelist.size() <= 10)
                    {
                        for(int i=articlelist.size()-1;i>=0;--i)
                        {
                            Article article = articleRepository.findById(articlelist.get(i).articleidx).orElse(null);
                            if(article != null)resultlist.add(article);
                        }
        
                    }
                    else {
                        for(int i=9;i>=0;--i)
                        {
                            Article article = articleRepository.findById(articlelist.get(i).articleidx).orElse(null);
                            if(article != null)resultlist.add(article);
                        }
                    }
                    for(int i=0;i<articlelist.size();++i)
                    {
                        System.out.println(articlelist.get(i).articleidx + " " + articlelist.get(i).likeynum);
                    }
                    
                    return resultlist;
    }

    @Override
    public List<Article> getarticlelistbykeyword(KeywordDto keyword) {
        List<Article> articlelist = articleRepository.findByHashnameItContainsKeyword(keyword.getKeyword());

        // 비공개 요소 삭제
        for (Iterator<Article> it = articlelist.iterator(); it.hasNext();) {
            Article value = it.next();
            if (value.getArticleshare() == 2)
                it.remove();
        }

        // userpid 유저가 search_pid 유저를 구독 안했을 경우 1번 요소도 삭제
        for (Iterator<Article> it = articlelist.iterator(); it.hasNext();) {
            Article value = it.next();
            if (value.getArticleshare() == 1) {
                Subscribe subscribe = subscribeRepository.findBySubfromAndSubto(keyword.getUserpid(),
                        value.getUserpid());
                if (subscribe == null)
                    it.remove();
            }

        }

        return articlelist;
    }

    @Transactional
    @Override
    public void SetShare(ArticleShareRequestDto articleShareRequestDto) {

        Article article = articleRepository.findByArticleidx(articleShareRequestDto.getArticleidx()).orElse(null);
        article.setArticleshare(articleShareRequestDto.getSharenum());
        articleRepository.save(article);
    }

    @Override
    public Article findByArticleidx(int articleidx) {
        Article article = articleRepository.findByArticleidx(articleidx).orElse(null);
        return article;
    }

    @Override
    public int getLikey(int articleidx) {
        Long likey = likeyRepository.articleLikeyCount(articleidx);
        int likei = Integer.parseInt(likey.toString());
        return likei;
    }

    @Override
    public List<String> getHash(int articleidx) {
        List<Hashtag> hashtaglist = hashtagRepository.findByArticleidx(articleidx);
        List<String> hashnamelist = new ArrayList<>();
        for (int i = 0; i < hashtaglist.size(); i++) {
            int hashidx = hashtaglist.get(i).getHashidx();
            Hash hash = hashRepository.findByHashidx(hashidx).orElse(null);
            if (hash != null) {
                String hashname = hash.getHashname();
                hashnamelist.add(hashname);
            }
        }
        return hashnamelist;
    }

    @Override
    public boolean getLikeystatus(int userpid, int articleidx) {
        Likey likey = likeyRepository.findByLikeyfromAndLikeyto(userpid, articleidx).orElse(null);
        if (likey == null) {
            return false;
        }
        return true;
    }

    @Override
    public int getTodayarticle(int userpid) {

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date time = new Date();

        String now = format.format(time);

        Article todayarticle = null;

        try {
            todayarticle = articleRepository.findByArticlecreatedateAndUserpid(now, userpid);
        } catch (Exception e) {
            todayarticle = null;
        }
        if (todayarticle == null)
            return -1;
        else
            return todayarticle.getArticleidx();
        // TODO Auto-generated method stub

    }


}
