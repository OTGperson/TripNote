package com.back.domain.favoriteDestination.repository;

import com.back.domain.favoriteDestination.entity.FavoriteDestination;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteDestinationRepository extends JpaRepository<FavoriteDestination, Long> {
}
