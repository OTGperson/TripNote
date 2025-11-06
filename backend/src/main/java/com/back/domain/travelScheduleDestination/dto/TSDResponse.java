package com.back.domain.travelScheduleDestination.dto;

import com.back.domain.travelScheduleDestination.entity.TSD;
import java.time.LocalTime;

public record TSDResponse(
  Long destinationId,
  String title,
  LocalTime visitStartTime,
  LocalTime visitEndTime,
  Integer orderNo,
  String note
) {
  public static TSDResponse from(TSD tsd) {
    return new TSDResponse(
      tsd.getDestination().getId(),
      tsd.getDestination().getTitle(),
      tsd.getVisitStartTime(),
      tsd.getVisitEndTime(),
      tsd.getOrderNo(),
      tsd.getNote()
    );
  }
}
