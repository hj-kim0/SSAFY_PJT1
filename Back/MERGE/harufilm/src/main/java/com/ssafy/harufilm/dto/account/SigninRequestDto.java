package com.ssafy.harufilm.dto.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SigninRequestDto {
    private String userid;
    private String userpassword;
    private String userfcmtoken;
}
