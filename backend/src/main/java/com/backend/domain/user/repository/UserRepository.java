package com.backend.domain.user.repository;

import com.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  User getUserByUsername(String username);
}
