package com.backend.domain.destination.dto;

import com.backend.domain.destination.entity.Destination;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DestinationSummary {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String title;
  private String areaCode;
  private String firstImage;
  private Integer contentTypeId;

  public DestinationSummary(Destination dest) {
    this.id = dest.getId();
    this.title = dest.getTitle();
    this.areaCode = dest.getAreaCode();
    this.firstImage = dest.getFirstImage();
    this.contentTypeId = dest.getContentTypeId();
  }
}
