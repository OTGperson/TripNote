package com.backend.domain.travelScheduleDestination.repository;

import com.backend.domain.travelScheduleDestination.entity.TSD;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TSDRepository extends JpaRepository<TSD, Long> {
}
