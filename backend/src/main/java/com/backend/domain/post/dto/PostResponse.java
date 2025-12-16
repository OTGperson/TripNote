package com.backend.domain.post.dto;

import com.backend.domain.post.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostResponse {

  private Long id;
  private Long authorId;
  private String title;
  private String content;
  private boolean isPublic;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static PostResponse from(Post post) {
    return PostResponse.builder()
      .id(post.getId())
      .authorId(post.getAuthorId())
      .title(post.getTitle())
      .content(post.getContent())
      .isPublic(post.isPublic())
      .createdAt(post.getCreatedAt())
      .updatedAt(post.getUpdatedAt())
      .build();
  }
}