package com.back.domain.travelPlan.repository;

import com.back.domain.travelPlan.entity.TravelPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelPlanRepository extends JpaRepository<TravelPlan, Long> {
}
