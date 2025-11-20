package com.backend.domain.destination.service;

import com.backend.domain.destination.repository.DestinationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DestinationService {
  private final DestinationRepository destinationRepository;
}
