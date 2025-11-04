package com.back.domain.destination.entity;

import com.back.domain.travelPlan.entity.TravelPlan;
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
  private String title;
  private String city;
  private String coverImageUrl;
  private String location;
  private String detail;
}
