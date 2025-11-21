package com.backend.domain.member.member.entity;

import com.backend.domain.member.member.enums.MemberRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
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