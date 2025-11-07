package com.back.domain.travelScheduleDestination.entity;

import com.back.domain.destination.entity.Destination;
import com.back.domain.travelSchedule.entity.TravelSchedule;
import jakarta.persistence.*;
import lombok.*;

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
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private TravelSchedule schedule;

  @ManyToOne
  private Destination destination;
}
