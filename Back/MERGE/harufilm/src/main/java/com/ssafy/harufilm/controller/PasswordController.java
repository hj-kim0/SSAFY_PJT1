package com.ssafy.harufilm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.harufilm.common.ErrorResponseBody;
import com.ssafy.harufilm.common.MessageBody;
import com.ssafy.harufilm.dto.account.ChangepwDto;
import com.ssafy.harufilm.dto.account.FindpwDto;
import com.ssafy.harufilm.entity.User;
import com.ssafy.harufilm.service.user.UserService;

@RestController
@RequestMapping("/api/account")
public class PasswordController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @PostMapping("/findpw")
    public ResponseEntity<?> pwqchk(@RequestBody FindpwDto findpwDto) {
        User user = new User();
        try {
            user = userService.getuserbyId(findpwDto.getUserid());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ErrorResponseBody.of(500, false,
                    "Internal Server Error, 서버 에러."));
        }
        if (user == null) {
            return ResponseEntity.status(400).body(ErrorResponseBody.of(400, false, "없는 아이디"));
        }
        if ((user.getUserpwq() == findpwDto.getUserpwq()) && (user.getUserpwa().equals(findpwDto.getUserpwa()))) {
            return ResponseEntity.status(200).body(MessageBody.of(true, "비밀 번호 질문 일치"));
        }
        return ResponseEntity.status(200).body(MessageBody.of(false, "비밀 번호 질문 불일치"));
    }

    @PostMapping("/changepw")
    public ResponseEntity<?> chgpw(@RequestBody ChangepwDto changepwDto) {
        try {
            userService.modifypassword(changepwDto.getUserid(), passwordEncoder.encode(changepwDto.getUsernewpw()));
            return ResponseEntity.status(200).body(MessageBody.of(true, "비밀 번호 변경"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(MessageBody.of(false, "비밀 번호 변경 실패"));
        }
    }
}
