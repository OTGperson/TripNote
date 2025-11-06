package com.back.domain.travelPlan.dto;

import com.back.domain.travelPlan.entity.TravelPlan;
import com.back.domain.user.dto.UserResponse;
import java.time.LocalDateTime;

public record TravelPlanResponse(
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
  public static TravelPlanResponse from(TravelPlan tp) {
    return new TravelPlanResponse(
      tp.getId(),
      tp.getTitle(),
      tp.getStartDate(),
      tp.getEndDate(),
      tp.isPublic(),
      tp.getContent(),
      tp.getCreatedAt(),
      tp.getUpdatedAt(),
      UserResponse.from(tp.getAuthor())
    );
  }
}
