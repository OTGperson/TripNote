package com.backend.domain.member.repository;

import com.backend.domain.member.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<Email, Long> {
  Email findByEmail(String email);
}
