package com.backend.global.security;

import com.backend.domain.member.member.repository.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtProvider jwtProvider;
  private final MemberRepository memberRepository;

  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
  ) throws ServletException, IOException {

    String bearerToken = request.getHeader("Authorization");

    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      String token = bearerToken.substring(7);

      if (jwtProvider.validate(token)) {
        Long memberId = jwtProvider.getMemberId(token);

        if (memberId != null) {
          memberRepository.findById(memberId).ifPresent(member -> {
            UsernamePasswordAuthenticationToken authentication =
              new UsernamePasswordAuthenticationToken(
                member, // principal
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + member.getRole().name()))
              );

            SecurityContextHolder.getContext().setAuthentication(authentication);
          });
        }
      }
    }

    filterChain.doFilter(request, response);
  }
}
