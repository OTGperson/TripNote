package com.backend.domain.member.controller;

import com.backend.domain.member.dto.MemberResponse;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.form.EmailCheckForm;
import com.backend.domain.member.form.EmailSendForm;
import com.backend.domain.member.form.MemberCreateForm;
import com.backend.domain.member.form.MemberLoginForm;
import com.backend.domain.member.service.EmailCheckService;
import com.backend.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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
  public MemberResponse<Member> signup(@Valid @RequestBody MemberCreateForm form, BindingResult bindingResult) {

    if(!form.getPassword1().equals(form.getPassword2())) {
      bindingResult.rejectValue("password2", "passwordInCorrect",
        "비밀번호가 일치하지 않습니다.");
    }

    if (!emailCheckService.isChecked(form.getEmail())) {
      bindingResult.rejectValue("email", "emailNotVerified",
        "이메일 인증이 아직 진행되지 않았습니다.");
    }

    if(bindingResult.hasErrors()) {
      MemberResponse<Member> response = new MemberResponse<>();
      response.setSuccess(false);
      response.setMessage("입력값을 다시 확인해주세요.");
      response.setData(null);
      return response;
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
      MemberResponse<Member> response = new MemberResponse<>();
      response.setSuccess(false);
      response.setMessage("이미 사용 중인 아이디/이메일입니다.");
      response.setData(null);
      return response;
    } catch (Exception e) {
      MemberResponse<Member> response = new MemberResponse<>();
      response.setSuccess(false);
      response.setMessage(e.getMessage());
      response.setData(null);
      return response;
    }

    MemberResponse<Member> response = new MemberResponse<>();
    response.setSuccess(true);
    response.setMessage("회원가입이 완료되었습니다.");
    response.setData(member);

    return response;
  }

  @PostMapping("/login")
  public Member login(@RequestBody MemberLoginForm form) {
    return memberService.login(
      form.getUsername(),
      form.getPassword()
    );
  }

  @PostMapping("/email/send-code")
  public MemberResponse<Member> sendEmailCode(@Valid @RequestBody EmailSendForm form, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      MemberResponse<Member> response = new MemberResponse<>();
      response.setSuccess(false);
      response.setMessage("입력값을 다시 확인해주세요.");
      response.setData(null);
      return response;
    }

    if (emailCheckService.isEmailAlreadyRegistered(form.getEmail())) {
      MemberResponse<Member> response = new MemberResponse<>();
      response.setSuccess(false);
      response.setMessage("이미 가입된 이메일입니다.");
      response.setData(null);
      return response;
    }

    boolean sent = emailCheckService.sendCode(form.getEmail());

    if (!sent) {
      MemberResponse<Member> response = new MemberResponse<>();
      response.setSuccess(false);
      response.setMessage("인증 메일 전송에 실패했습니다. 잠시 후 다시 시도해주세요.");
      response.setData(null);
      return response;
    }

    MemberResponse<Member> response = new MemberResponse<>();
    response.setSuccess(true);
    response.setMessage("인증 코드를 이메일로 전송했습니다.");
    response.setData(null);
    return response;
  }

  @PostMapping("/email/check-code")
  public MemberResponse<Member> checkEmailCode(@Valid @RequestBody EmailCheckForm form, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      MemberResponse<Member> response = new MemberResponse<>();
      response.setSuccess(false);
      response.setMessage("입력값을 다시 확인해주세요.");
      response.setData(null);
      return response;
    }

    boolean ok = emailCheckService.checkCode(form.getEmail(), form.getCode());

    if (!ok) {
      MemberResponse<Member> response = new MemberResponse<>();
      response.setSuccess(false);
      response.setMessage("인증번호가 올바르지 않거나 만료되었습니다.");
      response.setData(null);
      return response;
    }

    MemberResponse<Member> response = new MemberResponse<>();
    response.setSuccess(true);
    response.setMessage("이메일 인증이 완료되었습니다.");
    response.setData(null);
    return response;
  }
}
