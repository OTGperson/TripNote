package com.back.domain.favoriteDestination.dto;

import com.back.domain.favoriteDestination.entity.FavoriteDestination;

public record FavoriteDestinationResponse(
  Long destinationId,
  String title,
  String city,
  String thumbnail
) {
  public static FavoriteDestinationResponse from(FavoriteDestination fd) {
    return new FavoriteDestinationResponse(
      fd.getDestination().getId(),
      fd.getDestination().getTitle(),
      fd.getDestination().getCity(),
      fd.getDestination().getCoverImageUrl()
    );
  }
}
