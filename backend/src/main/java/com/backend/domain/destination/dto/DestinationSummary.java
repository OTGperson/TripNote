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
  private String addr1;
  private String areaCode;
  private String sigunguCode;
  private String firstImage;

  public DestinationSummary(Destination dest) {
    this.id = dest.getId();
    this.title = dest.getTitle();
    this.addr1 = (dest.getAddr1() != null && !dest.getAddr1().isBlank())
      ? dest.getAddr1()
      : "주소 정보 없음";
    this.areaCode = dest.getAreaCode();
    this.sigunguCode = dest.getSigunguCode();
    this.firstImage = dest.getFirstImage();
  }
}
