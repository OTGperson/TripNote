package com.backend.domain.member.dto;

import lombok.Setter;

@Setter
public class MemberResponse<T> {
  private boolean success;
  private String message;
  private T data;
}
