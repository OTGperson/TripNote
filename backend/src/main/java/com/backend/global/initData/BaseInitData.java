package com.backend.global.initData;

import com.backend.domain.member.member.service.MemberService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class BaseInitData {
  private final MemberService memberService;

  @Value("${custom.admin.username}")
  private String adminUsername;

  @Value("${custom.admin.password}")
  private String adminPassword;

  @Value("${custom.admin.nickname}")
  private String adminNickname;

  @Value("${custom.admin.email}")
  private String adminEmail;

  @Autowired
  @Lazy
  private BaseInitData self;

  public BaseInitData(MemberService memberService) {
    this.memberService = memberService;
  }

  @Bean
  public ApplicationRunner baseInitDataApplicationRunner() {
    return args -> {
      self.work1();
    };
  }

  @Transactional
  public void work1() {
    if (memberService.count() > 0) return;

    memberService.createAdmin(adminUsername, adminPassword, adminNickname, adminEmail);
    memberService.signup("user1", "1234", "userOne", "user1@test.com");
    memberService.signup("user2", "1234", "userTwo", "user2@test.com");
  }
}
