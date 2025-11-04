package com.back.domain.travelScheduleDestination.entity;

import com.back.domain.destination.entity.Destination;
import com.back.domain.travelSchedule.entity.TravelSchedule;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor @Builder
@Table(indexes = {
  @Index(name = "idx_tsd_schedule", columnList = "schedule_id"),
  @Index(name = "idx_tsd_destination", columnList = "destination_id")
})
public class TSD {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 일정
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "schedule_id", nullable = false)
  private TravelSchedule schedule;

  // 목적지
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "destination_id", nullable = false)
  private Destination destination;

  // 같은 일정 내에서의 순서
  private Integer orderNo;

  private String note;
}
