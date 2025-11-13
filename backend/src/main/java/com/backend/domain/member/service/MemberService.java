package com.backend.domain.member.service;

import com.backend.domain.member.entity.Member;
import com.backend.domain.member.enums.MemberRole;
import com.backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {
  private final MemberRepository memberRepository;

  public List<Member> findAll() {
    return memberRepository.findAll();
  }

  public long count() {
    return memberRepository.count();
  }

  public Member signup(String username, String password, String nickname, String email) {
    Member member = Member.builder()
      .username(username)
      .password(password)
      .nickname(nickname)
      .email(email)
      .role(MemberRole.USER)
      .build();

    return memberRepository.save(member);
  }

  public Member getMemberByUsername(String member) {
    return memberRepository.getMemberByUsername(member);
  }
}
