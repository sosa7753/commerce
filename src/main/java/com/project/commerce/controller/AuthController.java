package com.project.commerce.controller;

import com.project.commerce.domain.Member;
import com.project.commerce.dto.AuthDto;
import com.project.commerce.dto.AuthView;
import com.project.commerce.security.TokenProvider;
import com.project.commerce.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;

    private final TokenProvider tokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody AuthDto.SignUp request) {

        AuthView result = this.memberService.registerMember(request);
        log.info("계정 등록 완료");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody AuthDto.SignIn request) {

        Member member = this.memberService.logIn(request); // 비밀번호 검증
        log.info("비밀번호 검증 완료");
        String token = this.tokenProvider.generateToken(member); // 토큰 생성
        log.info("토큰 생성 완료");

        return ResponseEntity.ok(token);
    }

}
