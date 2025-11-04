package com.back.domain.travelSchedule.entity;

import com.back.domain.travelPlan.entity.TravelPlan;
import com.back.domain.travelScheduleDestination.entity.TSD;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TravelSchedule {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Long authorId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "travel_plan_id")
  private TravelPlan travelPlan;

  private String title;

  private LocalDateTime startDate;   // 전체 일정 시작 시각(선택)
  private LocalDateTime endDate;     // 전체 일정 종료 시각(선택)

  private boolean isPublic;

  private String content;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("orderNo ASC, id ASC")
  private List<TSD> destinations = new ArrayList<>();

  public void addDestination(TSD tsd) {
    tsd.setSchedule(this);
    this.destinations.add(tsd);
  }
}
