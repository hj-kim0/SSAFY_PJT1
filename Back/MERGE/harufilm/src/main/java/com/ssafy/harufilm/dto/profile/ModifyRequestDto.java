package com.ssafy.harufilm.dto.profile;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

// 정보 수정이 가능한 항목
public class ModifyRequestDto {

    private int userpid;

    private String username;

    private String userdesc;

    private MultipartFile userimg;

}
