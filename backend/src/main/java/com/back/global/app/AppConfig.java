package com.back.global.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

  // Static 필드
  private static Environment environment;
  private static ObjectMapper objectMapper;
  private static String siteFrontUrl;
  private static String siteBackUrl;
  private static String siteCookieDomain;
  private static String genFileDirPath;

  /**
   * BCrypt 암호화 빈 등록
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // ========== Setter 메서드 (Spring이 자동 주입) ==========

  @Autowired
  public void setEnvironment(Environment environment) {
    AppConfig.environment = environment;
  }

  @Autowired
  public void setObjectMapper(ObjectMapper objectMapper) {
    AppConfig.objectMapper = objectMapper;
  }

  @Value("${custom.site.frontUrl}")
  public void setSiteFrontUrl(String siteFrontUrl) {
    AppConfig.siteFrontUrl = siteFrontUrl;
  }

  @Value("${custom.site.backUrl}")
  public void setSiteBackUrl(String siteBackUrl) {
    AppConfig.siteBackUrl = siteBackUrl;
  }

  @Value("${custom.site.cookieDomain}")
  public void setSiteCookieDomain(String siteCookieDomain) {
    AppConfig.siteCookieDomain = siteCookieDomain;
  }

  @Value("${custom.genFile.dirPath}")
  public void setGenFileDirPath(String genFileDirPath) {
    AppConfig.genFileDirPath = genFileDirPath;
  }

  // ========== Profile 체크 메서드 ==========

  public static boolean isProd() {
    return environment.matchesProfiles("prod");
  }

  public static boolean isDev() {
    return environment.matchesProfiles("dev");
  }

  public static boolean isTest() {
    return environment.matchesProfiles("test");
  }

  public static boolean isNotProd() {
    return !isProd();
  }

  // ========== Getter 메서드 ==========

  public static ObjectMapper getObjectMapper() {
    return objectMapper;
  }

  public static String getSiteFrontUrl() {
    return siteFrontUrl;
  }

  public static String getSiteBackUrl() {
    return siteBackUrl;
  }

  public static String getSiteCookieDomain() {
    return siteCookieDomain;
  }

  public static String getGenFileDirPath() {
    return genFileDirPath;
  }
}
