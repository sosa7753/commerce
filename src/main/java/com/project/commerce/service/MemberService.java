package com.project.commerce.service;

import com.project.commerce.domain.Member;
import com.project.commerce.dto.AuthDto;
import com.project.commerce.dto.AuthView;
import com.project.commerce.exception.CommerceException;
import com.project.commerce.repository.MemberRepository;
import com.project.commerce.type.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MemberService(MemberRepository memberRepository,
                         @Qualifier("passwordEncoder") PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.memberRepository = memberRepository;
    }

    // 계정 등록
    @Transactional
    public AuthView registerMember(AuthDto.SignUp request) {

        boolean exists = this.memberRepository.existsByUsername(request.getUsername());

        if(exists) {
            throw new CommerceException(ErrorCode.ALREADY_USE_NAME);
        }

        request.setPassword(this.passwordEncoder.encode(request.getPassword()));
        Member createMember = request.authToEntity();
        this.memberRepository.save(createMember);

        return new AuthView(createMember);
    }


    public Member logIn(AuthDto.SignIn request) {
        Member member = this.memberRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CommerceException(ErrorCode.NOT_USER_NAME));

        if(!this.passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            log.info("비밀번호 불일치");
            throw new CommerceException(ErrorCode.NOT_EQUALS_PASSWORD);

        }
        return member;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.memberRepository.findByUsername(username)
                .orElseThrow(() -> new CommerceException(ErrorCode.NOT_USER_NAME));
    }
}
