package com.project.commerce.dto;

import com.project.commerce.domain.Member;
import com.project.commerce.domain.Role;
import com.project.commerce.type.UserType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class AuthView {
    private String username;
    private UserType userType;
    private List<String> roles;

    public AuthView(Member member) {
        this.username = member.getUsername();
        this.userType = member.getUserType();
        this.roles = member.getRoles().stream()
                .map(Role::getRole_name)
                .collect(Collectors.toList());
    }

}
