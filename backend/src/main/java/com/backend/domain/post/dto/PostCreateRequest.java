package com.backend.domain.post.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCreateRequest {
  private Long authorId;
  private String title;
  private String content;
  private Boolean isPublic;
}