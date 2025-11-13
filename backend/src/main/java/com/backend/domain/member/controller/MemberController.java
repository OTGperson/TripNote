package com.backend.domain.member.controller;

import com.backend.domain.member.entity.Member;
import com.backend.domain.member.form.MemberCreateForm;
import com.backend.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MemberController {
  private final MemberService memberService;

  @GetMapping("/member")
  public List<Member> getMembers() {
    return memberService.findAll();
  }



  @PostMapping("/member/signup")
  public String signup(@Valid @ModelAttribute MemberCreateForm form, BindingResult bindingResult) {
    if(bindingResult.hasErrors()) {
      return "redirect:/api/v1/member/signup";
    }

    if(!form.getPassword1().equals(form.getPassword2())) {
      bindingResult.rejectValue("password2", "passwordInCorrect",
        "비밀번호가 일치하지 않습니다.");
      return "redirect:/api/v1/member/signup";
    }

    try {
      memberService.signup(form.getUsername(), form.getPassword1(), form.getNickname(), form.getEmail());
    } catch (DataIntegrityViolationException e) {
      e.printStackTrace();
      bindingResult.reject("signupFailed", "이미 등록되었거나 사용 불가능한 ID입니다.");
      return "redirect:/api/v1/member/signup";
    } catch (Exception e) {
      e.printStackTrace();
      bindingResult.reject("signupFailed", e.getMessage());
      return "redirect:/api/v1/member/signup";
    }
    return "redirect:/";
  }
}
