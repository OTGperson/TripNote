package com.back.domain.favoriteDestination.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FaDe {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long user_id;
  private Long destination_id;
}
