package com.backend.domain.member.email.service;

import com.backend.domain.member.email.entity.Email;
import com.backend.domain.member.email.repository.EmailRepository;
import com.backend.domain.member.member.repository.MemberRepository;
import com.backend.global.dto.RsData;
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

  public RsData<Void> sendCode(String email) {
    // 이미 회원으로 가입된 이메일이면 false 리턴
    if (isEmailAlreadyRegistered(email)) {
      return RsData.fail("F-2", "이미 가입된 이메일입니다.");
    }

    String code = generateCode();
    LocalDateTime expireAt = LocalDateTime.now().plusMinutes(10);

    Email ec = emailRepository.findByEmail(email);
    if (ec == null) {
      ec = Email.builder()
        .email(email)
        .code(code)
        .expireAt(expireAt)
        .checked(false)
        .build();
    } else {
      ec.setCode(code);
      ec.setExpireAt(expireAt);
      ec.setChecked(false);
    }

    emailRepository.save(ec);

    boolean mailSent = emailSendService.sendMail(email, code);
    if(!mailSent) {
      return RsData.fail("F-3", "인증 메일 전송에 실패했습니다.");
    }

    return RsData.success("인증 코드를 이메일로 전송했습니다.");
  }

  public RsData<Void> checkCode(String email, String code) {
    Email ec = emailRepository.findByEmail(email);
    String input = code.toUpperCase();

    if (ec == null || ec.getExpireAt().isBefore(LocalDateTime.now()) || !ec.getCode().equals(input)) {
      return RsData.fail("F-4", "인증번호가 올바르지 않거나 만료되었습니다.");
    }

    ec.setChecked(true);
    emailRepository.save(ec);
    return RsData.success("이메일 인증이 완료되었습니다.");
  }

  public boolean isChecked(String email) {
    Email ec = emailRepository.findByEmail(email);
    return ec != null && ec.isChecked();
  }
}