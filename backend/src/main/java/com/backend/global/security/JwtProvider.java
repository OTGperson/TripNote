package com.backend.global.security;

import com.backend.domain.member.member.entity.Member;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

  @Value("${custom.jwt.secret-key}")
  private String secretKeyPlain;

  @Value("${jwt.access-token-expire-ms:3600000}") // 기본 1시간
  private long accessTokenExpireMs;

  private Key key;

  @PostConstruct
  public void init() {
    this.key = Keys.hmacShaKeyFor(secretKeyPlain.getBytes());
  }

  public String generateAccessToken(Member member) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + accessTokenExpireMs);

    return Jwts.builder()
      .setSubject(String.valueOf(member.getId()))
      .claim("username", member.getUsername())
      .claim("role", member.getRole().name())
      .setIssuedAt(now)
      .setExpiration(expiry)
      .signWith(key, SignatureAlgorithm.HS256)
      .compact();
  }

  public Jws<Claims> parse(String token) {
    return Jwts.parserBuilder()
      .setSigningKey(key)
      .build()
      .parseClaimsJws(token);
  }

  public Long getMemberId(String token) {
    try {
      Claims claims = parse(token).getBody();
      String sub = claims.getSubject();
      if (sub == null) return null;

      try {
        return Long.parseLong(sub);
      } catch (NumberFormatException e) {
        return null;
      }
    } catch (JwtException | IllegalArgumentException e) {
      return null;
    }
  }

  public boolean validate(String token) {
    try {
      parse(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }
}
