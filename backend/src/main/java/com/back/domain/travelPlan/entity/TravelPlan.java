package com.back.domain.travelPlan.entity;

import com.back.domain.user.entity.User;
import com.back.domain.travelSchedule.entity.TravelSchedule;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor @Builder
public class TravelPlan {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 작성자: FK 숫자 대신 연관관계로
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  private String title;

  @Column(name = "start_date")
  private LocalDateTime startDate;

  @Column(name = "end_date")
  private LocalDateTime endDate;

  @Column(name = "is_public")
  private boolean isPublic;

  @Column(length = 2000)
  private String content;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  // ⬇️ 일정들과의 관계. 목적지는 일정을 통해 연결됩니다.
  @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TravelSchedule> schedules = new ArrayList<>();
}
