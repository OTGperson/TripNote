package com.backend.domain.member.controller;

import com.backend.domain.member.entity.Member;
import com.backend.domain.member.form.MemberCreateForm;
import com.backend.domain.member.form.MemberLoginForm;
import com.backend.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
  public Map<String, Object> login(@RequestBody MemberLoginForm form) {
    Map<String, Object> result = new HashMap<>();

    Member member = memberService.login(
      form.getUsername(),
      form.getPassword()
    );

    // 로그인 실패 (아이디 없음 또는 비밀번호 틀림)
    if (member == null) {
      result.put("success", false);
      result.put("message", "아이디 또는 비밀번호가 일치하지 않습니다.");
      return result;
    }

    // 로그인 성공
    result.put("success", true);
    result.put("message", "로그인에 성공했습니다.");
    result.put("id", member.getId());
    result.put("username", member.getUsername());
    result.put("nickname", member.getNickname());
    result.put("email", member.getEmail());
    result.put("role", member.getRole().name());

    // 필요하면 전체 객체를 같이 내려줄 수도 있음 (password는 @JsonIgnore라 안 내려감)
    // result.put("member", member);

    return result;
  }

}
