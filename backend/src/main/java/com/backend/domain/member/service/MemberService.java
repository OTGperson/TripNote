package com.backend.domain.member.service;

import com.backend.domain.member.entity.Member;
import com.backend.domain.member.enums.MemberRole;
import com.backend.domain.member.repository.MemberRepository;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MemberService {
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  public List<Member> findAll() {
    return memberRepository.findAll();
  }

  public long count() {
    return memberRepository.count();
  }

  public Member signup(String username, String password, String nickname, String email) {
    Member member = Member.builder()
      .username(username)
      .password(passwordEncoder.encode(password))
      .nickname(nickname)
      .email(email)
      .role(MemberRole.USER)
      .build();

    return memberRepository.save(member);
  }

  public Member getMemberByUsername(String username) {
    return memberRepository.getMemberByUsername(username);
  }

  public Member login(String username, String password) {
    Member member = memberRepository.getMemberByUsername(username);

    if(member == null) {
      return null;
    }

    if(!passwordEncoder.matches(password, member.getPassword())) {
      return null;
    }

    return member;
  }
}
