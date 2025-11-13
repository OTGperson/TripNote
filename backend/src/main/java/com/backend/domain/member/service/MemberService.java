package com.backend.domain.member.service;

import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
  private final MemberRepository memberRepository;

  public Member getMemberByUsername(String username) {
    return memberRepository.getMemberByUsername(username);
  }

  public long count() {
    return memberRepository.count();
  }

  public void signup(String username, String password, String nickname, String email) {
    Member member = Member.builder()
      .username(username)
      .password(password)
      .nickname(nickname)
      .email(email)
      .build();

    memberRepository.save(member);
  }
}
