package com.backend.domain.destination.service;

import com.backend.domain.destination.entity.Destination;
import com.backend.domain.destination.repository.DestinationRepository;
import com.backend.global.dto.RsData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DestinationService {

  private final DestinationRepository destinationRepository;
  private final ObjectMapper objectMapper;

  @Value("${tourapi.base-url}")
  private String baseUrl;

  @Value("${tourapi.service-key}")
  private String serviceKey;

  private final RestTemplate restTemplate = new RestTemplate();

  public RsData<Void> importDestinationsByArea(String areaCode) {
    URI uri = UriComponentsBuilder
      .fromHttpUrl(baseUrl + "/areaBasedList2")
      .queryParam("serviceKey", serviceKey)
      .queryParam("MobileOS", "ETC")
      .queryParam("MobileApp", "TripNote")
      .queryParam("_type", "json")
      .queryParam("areaCode", areaCode)
      .queryParam("numOfRows", 1000)
      .queryParam("pageNo", 1)
      .build(true)
      .toUri();

    ResponseEntity<String> responseEntity =
      restTemplate.getForEntity(uri, String.class);

    if (!responseEntity.getStatusCode().is2xxSuccessful()) {
      return RsData.fail("F-7", "[TourApi] 호출 실패: " + responseEntity.getStatusCode());
    }

    try {
      String body = responseEntity.getBody();
      JsonNode root = objectMapper.readTree(body);

      JsonNode items = root
        .path("response")
        .path("body")
        .path("items")
        .path("item");

      if (items.isMissingNode() || !items.isArray()) {
        return RsData.fail("F-8", "[TourApi] areaCode=" + areaCode + " item 배열이 없습니다.");
      }

      for (JsonNode item : items) {
        long externalId = item.path("contentid").asLong();

        Destination dest = destinationRepository
          .findByExternalId(externalId)
          .orElseGet(() -> {
            Destination d = new Destination();
            d.setExternalId(externalId);
            return d;
          });

        dest.setTitle(item.path("title").asText(""));
        dest.setAddr1(item.path("addr1").asText(null));
        dest.setAddr2(item.path("addr2").asText(null));
        dest.setAreaCode(item.path("areacode").asText(null));
        dest.setSigunguCode(item.path("sigungucode").asText(null));
        dest.setFirstImage(item.path("firstimage").asText(null));
        // 12:관광지, 14:문화시설, 15:축제공연행사, 25:여행코스, 28:레포츠, 32:숙박, 38:쇼핑, 39:음식점
        dest.setContentTypeId(item.path("contenttypeid").asInt(0));

        destinationRepository.save(dest);
      }

      return RsData.success("[TourApi] areaCode=" + areaCode + " 여행지 동기화 완료");

    } catch (Exception e) {
      e.printStackTrace();
      return RsData.fail("F-9", "[TourApi] JSON 파싱 중 오류");
    }
  }

  public RsData<Void> importDestinationDetail(Destination dest) {
    if (dest.getExternalId() == null) {
      return RsData.fail("F-7", "[TourApi] 호출 실패");
    }

    try {
      URI uri = UriComponentsBuilder
        .fromHttpUrl(baseUrl + "/detailCommon2")
        .queryParam("serviceKey", serviceKey)
        .queryParam("MobileOS", "ETC")
        .queryParam("MobileApp", "TripNote")
        .queryParam("_type", "json")
        .queryParam("contentId", dest.getExternalId())
        .build(true)
        .toUri();

      ResponseEntity<String> responseEntity =
        restTemplate.getForEntity(uri, String.class);

      if (!responseEntity.getStatusCode().is2xxSuccessful()) {
        return RsData.fail("F-7", "[TourApi] 호출 실패: " + responseEntity.getStatusCode());
      }

      String body = responseEntity.getBody();
      JsonNode root = objectMapper.readTree(body);

      JsonNode items = root
        .path("response")
        .path("body")
        .path("items")
        .path("item");

      JsonNode firstItem;
      if (items.isArray()) {
        if (items.size() == 0) {
          return RsData.fail("F-8", "[TourApi] item 배열이 없습니다.");
        }
        firstItem = items.get(0);
      } else {
        firstItem = items;
      }

      String overview = firstItem.path("overview").asText(null);

      dest.setDetail(overview);

      destinationRepository.save(dest);

      return RsData.success("[TourApi] 여행지 정보 가져오기 완료");

    } catch (Exception e) {
      e.printStackTrace();
      return RsData.fail("F-9", "[TourApi] JSON 파싱 중 오류");
    }
  }

  public RsData<Void> importAllDestinationDetails() {
    List<Destination> allDestination = destinationRepository.findAll();

    int successCount = 0;
    int failCount = 0;

    for (Destination dest : allDestination) {
      RsData<Void> rs = importDestinationDetail(dest);
      if (rs.isSuccess()) {
        successCount++;
      } else {
        failCount++;
        System.out.println("[TourApi] detail 실패");
      }
    }

    if (failCount > 0) {
      return RsData.fail(
        "F-10",
        "[TourApi] 상세 가져오기 중 일부 실패 (성공: " + successCount + ", 실패: " + failCount + ")"
      );
    }

    return RsData.success("[TourApi] 모든 여행지 정보 가져오기 완료 (총 " + successCount + "건)");
  }

  public RsData<Void> syncAll() {
    // 1) 지역별 목록 동기화
//    for (int areaCode = 1; areaCode <= 17; areaCode++) {
      importDestinationsByArea(String.valueOf(1));
//    }

    // 2) 상세 정보 동기화
    importAllDestinationDetails();

    return RsData.success("[TourApi] 전국 여행지(목록 + 상세) 동기화 완료");
  }

  public List<Destination> findAll() {
    return destinationRepository.findAll();
  }

  public Optional<Destination> findById(Long id) {
    return destinationRepository.findById(id);
  }
}
