package com.backend.domain.travelSchedule.repository;

import com.backend.domain.travelSchedule.entity.TravelSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelScheduleRepository extends JpaRepository<TravelSchedule, Long> {
}
