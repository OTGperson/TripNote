package com.backend.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class RsData<T> {
  private final String resultCode;
  private final String message;
  private final T data;

  public static <T> RsData<T> of(String resultCode, String message) {
    return of(resultCode, message, null);
  }

  public static <T> RsData<T> of(String resultCode, String message, T data) {
    return new RsData<>(resultCode, message, data);
  }

  public boolean isSuccess() {
    return resultCode.startsWith("S-");
  }

  public boolean isFail() {
    return !isSuccess();
  }
}