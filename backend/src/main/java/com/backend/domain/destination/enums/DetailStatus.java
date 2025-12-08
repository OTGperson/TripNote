package com.backend.domain.destination.enums;

public enum DetailStatus {
  PENDING,   // 아직 시도 안함
  SUCCESS,   // 성공적으로 불러옴
  EMPTY,     // 호출 성공 but overview 없음
  ERROR      // 호출 실패
}
