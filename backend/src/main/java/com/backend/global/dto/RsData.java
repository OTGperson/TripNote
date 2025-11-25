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

  public static <T> RsData<T> of(String resultCode, String msg) {
    return of(resultCode, msg, null);
  }

  public static <T> RsData<T> of(String resultCode, String msg, T data) {
    return new RsData<>(resultCode, msg, data);
  }

  public boolean isSuccess() {
    return resultCode.startsWith("S-");
  }

  public boolean isFail() {
    return !isSuccess();
  }

  public static <T> RsData<T> success(String msg, T data) {
    return of("S-1", msg, data);
  }

  public static <T> RsData<T> success(String msg) {
    return of("S-1", msg);
  }

  public static <T> RsData<T> fail(String resultCode, String msg) {
    return of(resultCode, msg);
  }

  public static <T> RsData<T> fail(String msg) {
    return of("F-1", msg);
  }
}