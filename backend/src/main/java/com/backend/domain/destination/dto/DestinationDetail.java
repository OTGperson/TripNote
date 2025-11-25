package com.backend.domain.destination.dto;

import com.backend.domain.destination.entity.Destination;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class DestinationDetail {
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

  private String detail;
  private Integer contentTypeId;

  public DestinationDetail(Destination dest) {
    this.id = dest.getId();
    this.externalId = dest.getExternalId();
    this.title = dest.getTitle();
    this.addr1 = dest.getAddr1();
    this.addr2 = dest.getAddr2();
    this.areaCode = dest.getAreaCode();
    this.sigunguCode = dest.getSigunguCode();
    this.firstImage = dest.getFirstImage();
    this.detail = dest.getDetail();
    this.contentTypeId = dest.getContentTypeId();
  }
}
