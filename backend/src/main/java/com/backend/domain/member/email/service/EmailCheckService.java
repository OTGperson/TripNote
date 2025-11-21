package com.backend.domain.member.email.service;

import com.backend.domain.member.email.entity.Email;
import com.backend.domain.member.email.repository.EmailRepository;
import com.backend.domain.member.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailCheckService {

  private final EmailRepository emailRepository;
  private final EmailSendService emailSendService;
  private final MemberRepository memberRepository;

  private static final String CODE_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private static final int CODE_LENGTH = 6;
  private final SecureRandom random = new SecureRandom();

  private String generateCode() {
    StringBuilder sb = new StringBuilder(CODE_LENGTH);
    for (int i = 0; i < CODE_LENGTH; i++) {
      int idx = random.nextInt(CODE_CHARS.length());
      sb.append(CODE_CHARS.charAt(idx));
    }
    return sb.toString();
  }

  public boolean isEmailAlreadyRegistered(String email) {
    return memberRepository.existsByEmail(email);
  }

  public boolean sendCode(String email) {
    // 이미 회원으로 가입된 이메일이면 false 리턴
    if (isEmailAlreadyRegistered(email)) {
      return false;
    }

    String code = generateCode();
    LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);

    Email ev = emailRepository.findByEmail(email);
    if (ev == null) {
      ev = Email.builder()
        .email(email)
        .code(code)
        .expireAt(expiresAt)
        .checked(false)
        .build();
    } else {
      ev.setCode(code);
      ev.setExpireAt(expiresAt);
      ev.setChecked(false);
    }

    emailRepository.save(ev);

    boolean mailSent = emailSendService.sendMail(email, code);
    return mailSent;
  }

  public boolean checkCode(String email, String code) {
    Email ec = emailRepository.findByEmail(email);
    if (ec == null) return false;

    if (ec.getExpireAt().isBefore(LocalDateTime.now())) {
      return false;
    }

    String input = code.toUpperCase();
    if (!ec.getCode().equals(input)) {
      return false;
    }

    ec.setChecked(true);
    emailRepository.save(ec);
    return true;
  }

  public boolean isChecked(String email) {
    Email ec = emailRepository.findByEmail(email);
    return ec != null && ec.isChecked();
  }
}