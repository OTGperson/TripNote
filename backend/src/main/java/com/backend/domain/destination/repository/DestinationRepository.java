package com.backend.domain.destination.repository;

import com.backend.domain.destination.entity.Destination;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DestinationRepository extends JpaRepository<Destination, Long> {
  Optional<Destination> findByExternalId(Long externalId);

  List<Destination> findTop500ByDetailIsNull();
}
