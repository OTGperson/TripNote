package com.backend.domain.member.controller;

import com.backend.domain.member.entity.Member;
import com.backend.domain.member.form.EmailCheckForm;
import com.backend.domain.member.form.MemberCreateForm;
import com.backend.domain.member.form.MemberLoginForm;
import com.backend.domain.member.service.EmailCheckService;
import com.backend.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {
  private final MemberService memberService;
  private final EmailCheckService emailCheckService;

  @PostMapping("/signup")
  public Map<String, Object> signup(@RequestBody MemberCreateForm form) {
    Map<String, Object> result = new HashMap<>();

    if(!form.getPassword1().equals(form.getPassword2())) {
      throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }

    if (!emailCheckService.isChecked(form.getEmail())) {
      result.put("success", false);
      result.put("message", "이메일 인증을 먼저 완료해주세요.");
      return result;
    }

    Member member;
    try {
      member = memberService.signup(
        form.getUsername(),
        form.getPassword1(),
        form.getNickname(),
        form.getEmail()
      );
    } catch (DataIntegrityViolationException e) {
      result.put("success", false);
      result.put("message", "이미 사용 중인 아이디/이메일입니다.");
      return result;
    }

    result.put("success", true);
    result.put("message", "회원가입이 완료되었습니다.");
    result.put("id", member.getId());
    return result;
  }

  @PostMapping("/login")
  public Member login(@RequestBody MemberLoginForm form) {
    return memberService.login(
      form.getUsername(),
      form.getPassword()
    );
  }

  @PostMapping("/email/send-code")
  public Map<String, Object> sendEmailCode(@RequestBody Map<String, String> body) {
    String email = body.get("email");
    Map<String, Object> result = new HashMap<>();

    // 이미 가입된 이메일인지 먼저 확인
    if (emailCheckService.isEmailAlreadyRegistered(email)) {
      result.put("success", false);
      result.put("alreadyRegistered", true);
      result.put("message", "이미 가입된 이메일입니다.");
      return result;
    }

    boolean sent = emailCheckService.sendCode(email);

    if (!sent) {
      result.put("success", false);
      result.put("message", "인증 메일 전송에 실패했습니다. 잠시 후 다시 시도해주세요.");
      return result;
    }

    result.put("success", true);
    result.put("message", "인증 코드를 이메일로 전송했습니다.");
    return result;
  }

  // 2) 코드 검증
  @PostMapping("/email/check-code")
  public Map<String, Object> checkEmailCode(@RequestBody EmailCheckForm form) {
    Map<String, Object> result = new HashMap<>();

    boolean ok = emailCheckService.checkCode(form.getEmail(), form.getCode());

    if (!ok) {
      result.put("success", false);
      result.put("message", "인증번호가 올바르지 않거나 만료되었습니다.");
      return result;
    }

    result.put("success", true);
    result.put("message", "이메일 인증이 완료되었습니다.");
    return result;
  }
}
