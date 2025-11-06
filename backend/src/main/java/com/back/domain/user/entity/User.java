package com.back.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable=false, unique=true)
  private String email;

  @Column(nullable=false, unique=true)
  private String username;

  @Column(nullable=false)
  @JsonIgnore
  private String password;

  private String nickname;

  @Column(nullable=false)
  private String role;
}