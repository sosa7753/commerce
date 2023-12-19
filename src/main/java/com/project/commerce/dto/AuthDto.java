package com.project.commerce.dto;

import com.project.commerce.domain.Member;
import com.project.commerce.domain.Role;
import com.project.commerce.type.Authority;
import com.project.commerce.type.UserType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Arrays;

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
                    .my_location(this.myLocation)
                    .created_date(LocalDateTime.now())
                    .updated_date(LocalDateTime.now())
                    .roles(Arrays.asList(
                            Role.builder().role_name(Authority.ROLE_WRITE.name()).build(),
                            Role.builder().role_name(Authority.ROLE_READ.name()).build()))
                    .build();
        }
    }
}
