package com.backend.domain.favoriteDestination.controller;

import com.backend.domain.favoriteDestination.service.FavoriteDestinationService;
import com.backend.domain.member.member.entity.Member;
import com.backend.domain.member.member.service.MemberService;
import com.backend.global.dto.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/favorites")
public class FavoriteDestinationController {
  private final MemberService memberService;
  private final FavoriteDestinationService favoriteDestinationService;

  @PostMapping("/{destinationId}/toggle")
  public RsData<Boolean> toggleFavorite(@PathVariable Long destinationId, Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      return RsData.fail("F-1", "로그인 후 이용해주세요.");
    }

    Member member = (Member) authentication.getPrincipal();
    Long memberId = member.getId();

    return favoriteDestinationService.toggleFavorite(member, destinationId);
  }

  @GetMapping("/{destinationId}")
  public RsData<Boolean> isFavorite(@AuthenticationPrincipal Member member, @PathVariable Long destinatoinId) {
    boolean isFavorite = favoriteDestinationService.isFavorite(member, destinatoinId);
    return RsData.of("S-1", "조회 성공");
  }

  @GetMapping("/mypage")
  public List<Long> myFavorites(@AuthenticationPrincipal Member member) {
    List<Long> ids = favoriteDestinationService.findDestinationIdsByMember(member);
    return ids;
  }
}
