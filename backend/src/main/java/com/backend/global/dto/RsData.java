package com.backend.global.dto;

import lombok.Setter;

@Setter
public class MemberResponse<T> {
  private boolean success;
  private String message;
  private T data;
}
