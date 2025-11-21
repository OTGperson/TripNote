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
  public RsData<?> sendEmailCode(@Valid @RequestBody EmailSendForm form, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return RsData.fail("입력값을 다시 확인해주세요.");
    }

    return emailCheckService.sendCode(form.getEmail());
  }

  @PostMapping("/email/check-code")
  public RsData<?> checkEmailCode(@Valid @RequestBody EmailCheckForm form, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return RsData.fail("입력값을 다시 확인해주세요.");
    }

    return emailCheckService.checkCode(form.getEmail(), form.getCode());
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
      return RsData.fail("입력값을 다시 확인해주세요.");
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
      return RsData.fail("F-5", "이미 사용 중인 아이디입니다.");
    } catch (Exception e) {
      return RsData.fail("F-6", "회원가입 중 오류가 발생하였습니다.");
    }

    return RsData.success("회원가입이 완료되었습니다.", member);
  }

  @PostMapping("/login")
  public Member login(@RequestBody MemberLoginForm form) {
    return memberService.login(
      form.getUsername(),
      form.getPassword()
    );
  }
}
