package com.backend.domain.post.post.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCreateRequest {
  private String title;
  private String content;
  private Boolean isPublic;
}