package com.ssafy.harufilm.jwt;

import com.ssafy.harufilm.entity.User;
import com.ssafy.harufilm.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service("customUserDetailService")
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userid) throws UsernameNotFoundException {
        User user = userRepository.findByUserid(userid).orElse(null);
        if (user != null) {
            CustomUserDetails userDetails = new CustomUserDetails(user);
            return userDetails;
        }
        return null;

    }
}