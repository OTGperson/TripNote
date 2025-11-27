package com.backend.domain.member.member.repository;

import com.backend.domain.member.member.entity.Member;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long> {
  Member findByUsername(String username);

  @Modifying
  @Transactional
  @Query(value = "ALTER TABLE member AUTO_INCREMENT = 1", nativeQuery = true)
  void clearAutoIncrement();

  boolean existsByEmail(String email);
}
