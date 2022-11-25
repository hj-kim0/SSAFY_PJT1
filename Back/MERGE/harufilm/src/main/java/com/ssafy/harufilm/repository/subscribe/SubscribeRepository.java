package com.ssafy.harufilm.repository.subscribe;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ssafy.harufilm.entity.Subscribe;
import com.ssafy.harufilm.entity.User;

@Repository
public interface SubscribeRepository extends JpaRepository<Subscribe, Integer> {

    @Query("select u from User u where u.userpid in ( select s.subto from Subscribe s where s.subfrom = :userpid)")
    List<User> followList(@Param("userpid") int userpid);

    @Query("select u from User u where u.userpid in ( select s.subfrom from Subscribe s where s.subto = :userpid)")
    List<User> followerList(@Param("userpid") int userpid);

    <Optional> Subscribe findBySubfromAndSubto(int subfrom, int subto);

    List<Subscribe> findBySubfrom(int subfrom);

    List<Subscribe> findBySubto(int subto);
}
