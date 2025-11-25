package com.backend.domain.destination.entity;

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

  private String detail;

  private Integer contentTypeId;
}
