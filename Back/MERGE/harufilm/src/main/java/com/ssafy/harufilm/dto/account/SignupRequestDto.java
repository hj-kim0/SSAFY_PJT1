package com.ssafy.harufilm.dto.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class SignupRequestDto {

    private String userid;

    private String userpassword;

    private String username;

    private int userpwq;

    private String userpwa;
}
