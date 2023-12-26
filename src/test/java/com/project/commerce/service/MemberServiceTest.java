package com.project.commerce.service;

import com.project.commerce.config.AppConfig;
import com.project.commerce.domain.Member;
import com.project.commerce.dto.AuthDto;
import com.project.commerce.dto.AuthView;
import com.project.commerce.exception.CommerceException;
import com.project.commerce.repository.MemberRepository;
import com.project.commerce.type.ErrorCode;
import com.project.commerce.type.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@ExtendWith({MockitoExtension.class, SpringExtension.class})
@ContextConfiguration(classes = AppConfig.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private MemberService memberService;

    @BeforeEach
    void setup() {
        memberService = new MemberService(memberRepository, passwordEncoder);
    }

    @Test
    @DisplayName("계정 등록 성공")
    void success_signupTest() {
        //given
        AuthDto.SignUp request = new AuthDto.SignUp();
        request.setUsername("grace");
        request.setPassword("grace123");
        request.setUserType(UserType.ROLE_ADMIN);

        AuthView response = new AuthView();
        response.setUsername(request.getUsername());
        response.setUserType(request.getUserType());

        given(this.memberRepository.existsByUsername(any()))
                .willReturn(false);

        //when
        AuthView responseService = this.memberService.registerMember(request);
        //then
        assertEquals(responseService.getUsername(), response.getUsername());
        assertEquals(responseService.getUserType(), response.getUserType());
    }

    @Test
    @DisplayName("계정 등록 실패")
    void fail_signupTest() {
        //given
        AuthDto.SignUp request = new AuthDto.SignUp();
        request.setUsername("grace");
        request.setPassword("grace123");
        request.setUserType(UserType.ROLE_ADMIN);

        given(this.memberRepository.existsByUsername("grace"))
                .willReturn(true);

        //when
        //then
        assertThrows(CommerceException.class, () -> {
            this.memberService.registerMember(request);
        });
    }

    @Test
    @DisplayName("로그인 성공")
    void success_loginTest() {
        //given
        AuthDto.SignIn request = new AuthDto.SignIn();
        request.setUsername("grace");
        request.setPassword("grace123");

        Member grace = Member.builder()
                .username("grace")
                .password(this.passwordEncoder.encode(request.getPassword()))
                .build();

        given(this.memberRepository.findByUsername("grace"))
                .willReturn(Optional.of(grace));

        given(this.passwordEncoder.matches(request.getPassword(), grace.getPassword()))
                .willReturn(true);

        //when
        Member member = memberService.logIn(request);
        //then
        assertEquals(grace.getUsername(), member.getUsername());
        assertTrue(this.passwordEncoder.matches(
                request.getPassword(), member.getPassword()));
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void fail_wrongPassword_loginTest() {
        //given
        AuthDto.SignIn request = new AuthDto.SignIn();
        request.setUsername("grace");
        request.setPassword("grace123");

        Member grace = Member.builder()
                .username("grace")
                .password(this.passwordEncoder.encode(request.getPassword()))
                .build();

        given(this.memberRepository.findByUsername("grace"))
                .willReturn(Optional.of(grace));

        given(this.passwordEncoder.matches(request.getPassword(), grace.getPassword()))
                .willReturn(false);

        //then
        assertThrows(CommerceException.class, () -> this.memberService.logIn(request));
    }

    @Test
    @DisplayName("로그인 실패 - 유저가 존재하지 않음")
    void fail_user_loginTest() {
        //given
        AuthDto.SignIn request = new AuthDto.SignIn();
        request.setUsername("grace");
        request.setPassword("grace123");

        given(this.memberRepository.findByUsername("grace"))
                .willReturn(Optional.empty());

        //then
        assertThrows(CommerceException.class, () -> this.memberService.logIn(request));
    }
}