package com.backend.domain.destination.controller;

import com.backend.domain.destination.dto.DestinationDetail;
import com.backend.domain.destination.dto.DestinationSummary;
import com.backend.domain.destination.entity.Destination;
import com.backend.domain.destination.service.DestinationService;
import com.backend.global.dto.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/dest")
public class DestinationController {
  private final DestinationService destinationService;

  @GetMapping("list")
  public List<DestinationSummary> destinationsList() {
    List<Destination> destinations = destinationService.findAll();
    List<DestinationSummary> result = new ArrayList<>();
    for (Destination dest : destinations) {
      result.add(new DestinationSummary(dest));
    }
    return result;
  }

  @GetMapping("/{id}")
  public DestinationDetail destinationDetail(@PathVariable Long id) {
    Destination dest = destinationService.findById(id)
      .orElseThrow(() -> new RuntimeException("해당 여행지를 찾을 수 없습니다."));

    return new DestinationDetail(dest);
  }

  @PostMapping("/admin/sync")
  public RsData<Void> syncAll() {
    return destinationService.syncAll();
  }
}
