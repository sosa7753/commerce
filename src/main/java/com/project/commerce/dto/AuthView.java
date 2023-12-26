package com.project.commerce.dto;

import com.project.commerce.domain.Member;
import com.project.commerce.type.UserType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthView {
    private String username;
    private UserType userType;

    public AuthView(Member member) {
        this.username = member.getUsername();
        this.userType = member.getUserType();
    }

}
