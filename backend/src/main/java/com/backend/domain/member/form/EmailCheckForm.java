package com.backend.domain.member.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailCheckForm {
  private String email;
  private String code;
}