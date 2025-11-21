package com.backend.domain.member.member.controller;

import com.backend.global.dto.RsData;
import com.backend.domain.member.member.entity.Member;
import com.backend.domain.member.email.form.EmailCheckForm;
import com.backend.domain.member.email.form.EmailSendForm;
import com.backend.domain.member.member.form.MemberCreateForm;
import com.backend.domain.member.member.form.MemberLoginForm;
import com.backend.domain.member.email.service.EmailCheckService;
import com.backend.domain.member.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {
  private final MemberService memberService;
  private final EmailCheckService emailCheckService;

  @PostMapping("/email/send-code")
  public RsData<Member> sendEmailCode(@Valid @RequestBody EmailSendForm form, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return RsData.of("F-1", "입력값을 다시 확인해주세요.");
    }

    if (emailCheckService.isEmailAlreadyRegistered(form.getEmail())) {
      return RsData.of("F-2", "이미 가입된 이메일입니다.");
    }

    boolean sent = emailCheckService.sendCode(form.getEmail());

    if (!sent) {
      return RsData.of("F-3", "인증 메일 전송에 실패했습니다.");
    }

    return RsData.of("S-1", "인증 코드를 이메일로 전송했습니다.");
  }

  @PostMapping("/email/check-code")
  public RsData<Member> checkEmailCode(@Valid @RequestBody EmailCheckForm form, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return RsData.of("F-1", "입력값을 다시 확인해주세요.");
    }

    boolean ok = emailCheckService.checkCode(form.getEmail(), form.getCode());

    if (!ok) {
      return RsData.of("F-4", "인증번호가 올바르지 않거나 만료되었습니다.");
    }

    return RsData.of("S-2", "이메일 인증이 완료되었습니다.");
  }

  @PostMapping("/signup")
  public RsData<Member> signup(@Valid @RequestBody MemberCreateForm form, BindingResult bindingResult) {

    if(!form.getPassword1().equals(form.getPassword2())) {
      bindingResult.rejectValue("password2", "passwordInCorrect",
        "비밀번호가 일치하지 않습니다.");
    }

    if (!emailCheckService.isChecked(form.getEmail())) {
      bindingResult.rejectValue("email", "emailNotVerified",
        "이메일 인증이 아직 진행되지 않았습니다.");
    }

    if(bindingResult.hasErrors()) {
      return RsData.of("F-1", "입력값을 다시 확인해주세요.");
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
      return RsData.of("F-5", "이미 사용 중인 아이디입니다.");
    } catch (Exception e) {
      return RsData.of("F-6", "회원가입 중 오류가 발생하였습니다.");
    }

    return RsData.of("S-3", "회원가입이 완료되었습니다.");
  }

  @PostMapping("/login")
  public Member login(@RequestBody MemberLoginForm form) {
    return memberService.login(
      form.getUsername(),
      form.getPassword()
    );
  }
}
