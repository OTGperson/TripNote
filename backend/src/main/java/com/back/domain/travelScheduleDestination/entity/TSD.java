package com.back.domain.travelScheduleDestination.entity;

import com.back.domain.destination.entity.Destination;
import com.back.domain.travelSchedule.entity.TravelSchedule;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "travel_schedule_destination",
  indexes = {
    @Index(name = "idx_tsd_schedule", columnList = "schedule_id"),
    @Index(name = "idx_tsd_destination", columnList = "destination_id")
  })
public class TSD {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "schedule_id", nullable = false)
  private TravelSchedule schedule;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "destination_id", nullable = false)
  private Destination destination;

  // 같은 일정 내 순서/시간/메모 — “방문”에 귀속된 정보
  private Integer orderNo;

  private LocalTime visitStartTime;
  private LocalTime visitEndTime;

  @Column(length = 1000)
  private String note;
}
