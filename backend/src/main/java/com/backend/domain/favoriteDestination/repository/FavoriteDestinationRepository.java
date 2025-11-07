package com.backend.domain.favoriteDestination.repository;

import com.backend.domain.favoriteDestination.entity.FavoriteDestination;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteDestinationRepository extends JpaRepository<FavoriteDestination, Long> {
}
