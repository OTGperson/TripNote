package com.backend.domain.destination.entity;

import com.backend.domain.destination.enums.DetailStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Destination {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private Long externalId;

  private String title;

  private String addr1;
  private String addr2;

  private String areaCode;
  private String sigunguCode;

  private String firstImage;

  @Lob
  @Column(columnDefinition = "TEXT")
  private String detail;

  private Integer contentTypeId;

  @Enumerated(EnumType.STRING)
  private DetailStatus detailStatus;

  @Builder
  public Destination(Long externalId, String detail, DetailStatus detailStatus) {
    this.externalId = externalId;
    this.detail = detail;
    this.detailStatus = detailStatus != null ? detailStatus : DetailStatus.PENDING;
  }

  public void markDetailSuccess(String overview) {
    if (overview == null || overview.isBlank()) {
      this.detail = "";
      this.detailStatus = DetailStatus.EMPTY;
    } else {
      this.detail = overview;
      this.detailStatus = DetailStatus.SUCCESS;
    }
  }

  public void markDetailError() {
    this.detailStatus = DetailStatus.ERROR;
  }
}
