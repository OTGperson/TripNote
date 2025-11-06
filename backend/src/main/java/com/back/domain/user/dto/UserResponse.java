package com.back.domain.user.dto;

import com.back.domain.user.entity.User;

public record UserResponse(
  Long id,
  String email,
  String username,
  String nickname,
  String role
) {
  public static UserResponse from(User user) {
    return new UserResponse(
      user.getId(),
      user.getEmail(),
      user.getUsername(),
      user.getNickname(),
      user.getRole()
    );
  }
}