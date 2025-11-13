package com.backend.domain.member.entity;

import com.backend.domain.member.enums.MemberRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Member {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique=true)
  private String email;

  @Column(unique=true)
  private String username;

  @JsonIgnore
  private String password;

  private String nickname;

  @Enumerated(EnumType.STRING)
  private MemberRole role = MemberRole.USER;
}