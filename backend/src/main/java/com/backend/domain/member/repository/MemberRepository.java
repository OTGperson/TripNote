package com.backend.domain.member.repository;

import com.backend.domain.member.entity.Member;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long> {
  Member getMemberByUsername(String username);

  @Modifying
  @Transactional
  @Query(value = "ALTER TABLE member AUTO_INCREMENT = 1", nativeQuery = true)
  void clearAutoIncrement();

  Member findByEmail(String email);
}
