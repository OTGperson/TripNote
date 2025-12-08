package com.backend.domain.member.member.dto;

import com.backend.domain.member.member.entity.Member;
import com.backend.domain.member.member.enums.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class LoginResponse {
  private String accessToken;
  private Long id;
  private String username;
  private String nickname;
  private MemberRole role;

  public static LoginResponse from(Member member, String accessToken) {
    return new LoginResponse(
      accessToken,
      member.getId(),
      member.getUsername(),
      member.getNickname(),
      member.getRole()
    );
  }
}