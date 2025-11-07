package com.backend.domain.user.controller;

import com.backend.domain.user.entity.User;
import com.backend.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
  private final UserService userService;

  @PostMapping("/signup")
  public String signup(String username, String password, String email, HttpServletRequest req) {
    User oldUser = userService.getUserByUsername(username);

    String passwordClearText = password;
//    password = PasswordEncoder.encode(password);

//    return userService.signup();
    return "0";
  }
}
