package com.back.domain.travelPlan.entity;

import com.back.domain.travelSchedule.entity.TravelSchedule;
import com.back.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(indexes = {
  @Index(name = "idx_plan_owner_created", columnList = "author_id, created_at"),
  @Index(name = "idx_plan_public", columnList = "is_public")
})
public class TravelPlan {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  @Column(nullable = false, length = 120)
  private String title;

  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @Column(name = "end_date", nullable = false)
  private LocalDate endDate;

  @Column(name = "is_public", nullable = false)
  private boolean isPublic;

  @Column(length = 2000)
  private String memo;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TravelSchedule> schedules = new ArrayList<>();
}