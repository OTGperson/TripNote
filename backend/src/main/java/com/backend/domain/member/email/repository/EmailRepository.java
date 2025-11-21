package com.backend.domain.member.email.repository;

import com.backend.domain.member.email.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<Email, Long> {

  Email findByEmail(String email);
}
