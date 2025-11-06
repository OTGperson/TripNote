package com.back.domain.destination.dto;

import com.back.domain.destination.entity.Destination;

public record DestinationResponse(
  Long id,
  String title,
  String city,
  String coverImageUrl,
  String location,
  String detail
) {
  public static DestinationResponse from(Destination d) {
    return new DestinationResponse(
      d.getId(),
      d.getTitle(),
      d.getCity(),
      d.getCoverImageUrl(),
      d.getLocation(),
      d.getDetail()
    );
  }
}
