package com.backend.domain.post.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Post {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long author_id;
  private String title;

  @Column(columnDefinition = "TEXT")
  private String content;

  private boolean is_public;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime created_at;

  @LastModifiedDate
  private LocalDateTime updated_at;
}
