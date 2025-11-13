package com.backend.domain.home.controller;

import com.backend.global.rq.Rq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {
  private  final Rq rq;

  @GetMapping("/")
  @ResponseBody
  public String home() {
    // 로컬 호스트 정보를 저장하는 변수
    InetAddress localHost = null;

    try {
      localHost = InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      throw new RuntimeException(e);
    }

    log.debug("Run in dev/prod environments");

    return "main(version : 0.0.1), hostname : %s".formatted(localHost.getHostName());
  }
}
