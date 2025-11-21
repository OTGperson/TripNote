package com.backend.domain.member.email.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSendService {

  private final JavaMailSender mailSender;

  @Value("${spring.mail.username}")
  private String fromEmail;

  public boolean sendMail(String toEmail, String code) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

      helper.setTo(toEmail);
      helper.setFrom(fromEmail);
      helper.setSubject("[TripNote] 이메일 인증 코드");

      String text = """
          TripNote 회원가입을 위한 이메일 인증 코드입니다.

          인증 코드: %s

          이 코드는 10분 동안 유효합니다.
          """.formatted(code);

      helper.setText(text, false);

      mailSender.send(message);
      System.out.println("[메일전송 성공] to=" + toEmail + ", code=" + code);
      return true;
    } catch (Exception e) {
      System.out.println("[메일전송 실패] to=" + toEmail);
      e.printStackTrace();
      return false;
    }
  }
}