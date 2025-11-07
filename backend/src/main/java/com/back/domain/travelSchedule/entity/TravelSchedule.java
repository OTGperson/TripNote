package com.back.domain.travelSchedule.entity;

import com.back.domain.travelPlan.entity.TravelPlan;
import com.back.domain.travelScheduleDestination.entity.TSD;
import com.back.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "travel_schedule", indexes = {
  @Index(name = "idx_schedule_plan_date_order", columnList = "plan_id, schedule_date, order_no")
})
public class TravelSchedule {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  private TravelPlan plan;

  @ManyToOne(fetch = FetchType.LAZY)
  private User author;

  private String title;
  private LocalDate scheduleDate;
  private LocalTime startTime;
  private LocalTime endTime;
  private Integer orderNo;
  private boolean isPublic;
  private String note;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "schedule", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  @OrderBy("orderNo ASC, id ASC")
  private List<TSD> destinations = new ArrayList<>();
}