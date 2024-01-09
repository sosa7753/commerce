package com.project.commerce.dto;

import com.project.commerce.domain.Member;
import com.project.commerce.type.UserType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AuthView {
    private String username;
    private UserType userType;

    private LocalDateTime createdAt;

    public AuthView(Member member) {
        this.username = member.getUsername();
        this.userType = member.getUserType();
        this.createdAt = member.getCreatedAt();
    }
}
