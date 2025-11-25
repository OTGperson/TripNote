package com.backend.domain.destination.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DestinationSyncService {
  private final DestinationService destinationService;

  @Scheduled(cron = "0 0 4 * * *")
  public void syncEveryDay() {
    destinationService.syncAll();
  }
}
