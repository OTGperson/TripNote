package com.backend.domain.member.controller;

import com.backend.domain.member.entity.Member;
import com.backend.domain.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {
  private final MemberService memberService;

  @PostMapping("/signup")
  public String signup(String username, String password, String email, String nickname, HttpServletRequest req) {
    memberService.signup(username, password, nickname, email);

    return "redirect:/";
  }
}
