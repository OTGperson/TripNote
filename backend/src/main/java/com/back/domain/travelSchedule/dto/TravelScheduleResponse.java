package com.back.domain.travelSchedule.dto;

import com.back.domain.travelSchedule.entity.TravelSchedule;
import com.back.domain.user.dto.UserResponse;
import java.time.LocalDateTime;

public record TravelScheduleResponse(
  Long id,
  String title,
  LocalDateTime startDate,
  LocalDateTime endDate,
  boolean isPublic,
  String content,
  LocalDateTime createdAt,
  LocalDateTime updatedAt,
  UserResponse author
) {
  public static TravelScheduleResponse from(TravelSchedule ts) {
    return new TravelScheduleResponse(
      ts.getId(),
      ts.getTitle(),
      ts.getStartDate(),
      ts.getEndDate(),
      ts.isPublic(),
      ts.getContent(),
      ts.getCreatedAt(),
      ts.getUpdatedAt(),
      UserResponse.from(ts.getAuthor())
    );
  }
}
