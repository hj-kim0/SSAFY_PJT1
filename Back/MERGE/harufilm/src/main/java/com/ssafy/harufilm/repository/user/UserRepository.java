package com.ssafy.harufilm.repository.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ssafy.harufilm.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUserid(String userid);

    User findByUserpid(int userpid);

    @Query("select u from User u where u.username like %?1% or u.userid like %?2%")
    // List<User> findByUsernameContaining(@Param("keyword") String keyword,
    // @Param("key") String key);
    List<User> findByUsernameOrUseridContaining(String keyword, String key);
}
