package com.project.commerce.service;

import com.project.commerce.repository.MemberRepository;
import com.project.commerce.security.TokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TokenProvider tokenProvider;

    @InjectMocks
    private MemberService memberService;


    @Test
    void success_signupTest() {
        //given

        given(this.memberRepository.existsByUsername(any()));
        //when
        //then
    }
}