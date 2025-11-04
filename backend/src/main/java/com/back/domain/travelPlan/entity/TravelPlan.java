package com.back.domain.travelPlan.entity;

import com.back.domain.destination.entity.Destination;
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
public class TravelPlan {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Long author_id;

  @OneToMany(mappedBy = "travelPlan", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Destination> destinations = new ArrayList<>();

  private String title;
  private LocalDateTime start_date;
  private LocalDateTime end_date;
  private boolean is_public;
  private String content;
  private LocalDateTime created_at;
  private LocalDateTime updated_at;


}
