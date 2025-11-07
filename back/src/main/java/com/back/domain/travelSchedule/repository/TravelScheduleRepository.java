package com.back.domain.travelSchedule.repository;

import com.back.domain.travelSchedule.entity.TravelSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelScheduleRepository extends JpaRepository<TravelSchedule, Long> {
}
