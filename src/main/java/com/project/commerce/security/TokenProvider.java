package com.project.commerce.security;

import com.project.commerce.domain.Member;
import com.project.commerce.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    private static final long TOKEN_EXPIRE_TIME = 2000 * 60 * 60; // 2시간

    private final MemberService memberService;

    @Value("{spring.jwt.secret}")
    private String secretKey;

    // jwt 토큰 생성
    public String generateToken(Member member) {
        Claims claims = Jwts.claims().setSubject(member.getUsername());

        claims.put("userType", member.getUserType()); // 계정 타입

        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS512, this.secretKey)
                .compact();
    }

    // 유효한 토큰인지 체크
    public boolean validateToken(String token) {
        if(!StringUtils.hasText(token)) { // 토큰이 null 이거나 공백인지 판별
            return false;
        }

        Claims claims = this.parsingClaims(token);
        return !claims.getExpiration().before(new Date());
    }

    /**  토큰 유효성 검증
     *  1. jwt로부터 인증정보 가져오기
     *  2. 파싱된 데이터에서 사용자 정보 얻기
     *  3. 비밀키로 파싱하기
     */

    public Authentication getAuthentication(String jwt) {
        UserDetails userDetails = this.memberService.loadUserByUsername(this.parsedUsername(jwt));
        return new UsernamePasswordAuthenticationToken(
                userDetails,"", userDetails.getAuthorities());
    }

    private String parsedUsername(String jwt) {
        return this.parsingClaims(jwt).getSubject();
    }

    private Claims parsingClaims(String jwt) {
        try {
            return Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(jwt).getBody();
        }catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
