package com.backend.domain.travelPlan.repository;

import com.backend.domain.travelPlan.entity.TravelPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelPlanRepository extends JpaRepository<TravelPlan, Long> {
}
