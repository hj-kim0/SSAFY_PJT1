package com.ssafy.harufilm.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private int userpid;

    @Column
    private String userid;

    @Column
    private String username;

    @Column
    private String userpassword;

    @Column
    private String userimg;

    @Column
    private String userdesc;
    
    @Column
    private int userpwq;
    
    @Column
    private String userpwa;

    @Column(updatable = false, length = 10)
    private String roles;

    public List<String> getRoleList() {
        if (this.roles.length() > 0) {
            return Arrays.asList(this.roles.split(","));
        }
        return new ArrayList<>();

    }
    @Column
    private String userfcmtoken;
}
