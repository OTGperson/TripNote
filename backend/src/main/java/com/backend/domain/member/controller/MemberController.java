package com.backend.domain.member.controller;

import com.backend.domain.member.entity.Member;
import com.backend.domain.member.form.MemberCreateForm;
import com.backend.domain.member.form.MemberLoginForm;
import com.backend.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {
  private final MemberService memberService;

  @GetMapping
  public List<Member> getMembers() {
    return memberService.findAll();
  }

  @PostMapping("/signup")
  public Member signup(@RequestBody MemberCreateForm form) {
    if(!form.getPassword1().equals(form.getPassword2())) {
      throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }

    return memberService.signup(
      form.getUsername(),
      form.getPassword1(),
      form.getNickname(),
      form.getEmail()
    );
  }

  @PostMapping("/login")
  public Member login(@RequestBody MemberLoginForm form) {
    return memberService.login(
      form.getUsername(),
      form.getPassword()
    );
  }
}
