package com.ssafy.harufilm.dto.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangepwDto {
    
    private String userid;
    private String userpw;
    private String usernewpw;
}
