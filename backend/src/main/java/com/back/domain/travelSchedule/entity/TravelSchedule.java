package com.back.domain.travelSchedule.entity;

import com.back.domain.travelPlan.entity.TravelPlan;
import com.back.domain.travelScheduleDestination.entity.TSD;
import com.back.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "travel_schedule", indexes = {
  @Index(name = "idx_schedule_plan_date_order", columnList = "plan_id, schedule_date, order_no")
})
public class TravelSchedule {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 어떤 여행(Plan)에 속한 일정인지
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "plan_id", nullable = false)
  private TravelPlan plan;

  // 작성자(선택: 작성자 추적이 필요 없다면 제거 가능)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  @Column(nullable = false, length = 120)
  private String title;

  // 일정이 속한 '날짜'
  @Column(name = "schedule_date", nullable = false)
  private LocalDate scheduleDate;

  // 시간대는 선택 필드
  @Column(name = "start_time")
  private LocalTime startTime;

  @Column(name = "end_time")
  private LocalTime endTime;

  // 같은 날짜 내에서 정렬용(드래그 정렬 등)
  @Column(name = "order_no", nullable = false)
  private Integer orderNo;

  // 공개 여부(필요 없으면 제거 가능)
  @Column(name = "is_public", nullable = false)
  private boolean isPublic;

  // 메모/설명(필요 없으면 제거 가능)
  @Column(name = "note", length = 2000)
  private String note;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  // 이 일정에 포함된 방문지들 (연결 엔티티 사용 시)
  @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("orderNo ASC, id ASC")
  private List<TSD> destinations = new ArrayList<>();
}