package com.backend.domain.travelPlan.entity;

import com.backend.domain.travelSchedule.entity.TravelSchedule;
import com.backend.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

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
public class TravelPlan {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Member author;

  private String title;
  private LocalDate startDate;
  private LocalDate endDate;
  private boolean isPublic;
  private String memo;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "plan", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  private List<TravelSchedule> schedules = new ArrayList<>();
}