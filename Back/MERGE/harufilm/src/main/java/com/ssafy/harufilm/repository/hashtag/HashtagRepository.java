package com.ssafy.harufilm.repository.hashtag;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.harufilm.entity.Hashtag;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Integer> {
    // @Query("select article from Article a where a.articleidx = (select articleidx
    // from Hashtag ht where ht.hashidx = (select hashidx from Hash h h.hasname =
    // :hashname)))")
    // List<Article> findByHashname(String hashname);

    void findAllByArticleidx(int articleidx);

    void deleteAllByArticleidx(int articleidx);

    List<Hashtag> findByArticleidx(int articleidx);

}
