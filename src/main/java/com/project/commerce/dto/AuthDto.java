package com.project.commerce.dto;

import com.project.commerce.domain.Member;
import com.project.commerce.type.UserType;
import lombok.Builder;
import lombok.Data;

@Builder
public class AuthDto {

    @Data
    public static class SignIn {
        private String username;
        private String password;
    }

    @Data
    public static class SignUp {
        private String username;
        private String password;
        private UserType userType;

        private String myLocation;

        public Member authToEntity() {
            return Member.builder()
                    .username(this.username)
                    .password(this.password)
                    .userType(this.userType)
                    .myLocation(this.myLocation)
                    .build();
        }
    }
}
