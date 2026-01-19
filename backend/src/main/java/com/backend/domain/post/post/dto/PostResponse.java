package com.backend.domain.post.post.dto;

import com.backend.domain.post.post.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

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

  private List<String> imageUrls;

  public static PostResponse from(Post post, List<String> imageUrls) {
    return PostResponse.builder()
      .id(post.getId())
      .authorId(post.getAuthorId())
      .title(post.getTitle())
      .content(post.getContent())
      .isPublic(post.isPublic())
      .createdAt(post.getCreatedAt())
      .updatedAt(post.getUpdatedAt())
      .imageUrls(imageUrls != null ? imageUrls : Collections.emptyList())
      .build();
  }
}