package com.ssafy.harufilm.service.user;

import java.io.IOException;
import java.util.List;

import com.ssafy.harufilm.dto.account.SignupRequestDto;
import com.ssafy.harufilm.dto.account.SmallProfileResponseDto;
import com.ssafy.harufilm.dto.profile.ModifyRequestDto;
import com.ssafy.harufilm.entity.User;

public interface UserService {

    User getuserbyId(String userid);

    User getuserbyPid(int userpid);

    User userNewSave(SignupRequestDto signupRequestDto);

    void modifyprofile(ModifyRequestDto modifyRequestDto) throws IllegalStateException, IOException;

    List<SmallProfileResponseDto> getuserlistbykeyword(String keyword);

    void modifypassword(String userid, String enuserpassword);

    void signdown(User user);

    void setuserfcmtoken(int userpid,String userfcmtoken);

    String getuserfcmtoken(int userpid);

    void deletefcmtoken(int userpid);

}
