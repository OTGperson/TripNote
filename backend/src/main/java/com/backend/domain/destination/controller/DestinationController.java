package com.backend.domain.destination.controller;

import com.backend.domain.destination.dto.DestinationDetail;
import com.backend.domain.destination.dto.DestinationSummary;
import com.backend.domain.destination.entity.Destination;
import com.backend.domain.destination.service.DestinationService;
import com.backend.global.dto.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/dest")
public class DestinationController {
  private final DestinationService destinationService;

  @GetMapping
  public List<DestinationSummary> getDestinations() {
    List<Destination> destinations = destinationService.findAll();

    List<DestinationSummary> result = new ArrayList<>();

    for (Destination dest : destinations) {
      DestinationSummary ds = new DestinationSummary(dest);

      result.add(ds);
    }

    return result;
  }

  // 상세 조회
  @GetMapping("/{id}")
  public DestinationDetail getDestination(@PathVariable Long id) {
    Destination dest = destinationService.findById(id)
      .orElseThrow(() -> new RuntimeException("해당 여행지를 찾을 수 없습니다."));

    return new DestinationDetail(dest);
  }

  @PostMapping("/import/{areaCode}")
  public RsData<String> importByArea(@PathVariable String areaCode) {
    return destinationService.importDestinationsByArea(areaCode);
  }
}
