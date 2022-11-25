package com.ssafy.harufilm.repository.hash;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.harufilm.entity.Hash;

@Repository
public interface HashRepository extends JpaRepository<Hash, Integer>{
    Optional<Hash> findByHashname(String hashname);

    Optional<Hash> findByHashidx(int hashidx);
}
