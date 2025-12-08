package com.backend.domain.favoriteDestination.service;

import com.backend.domain.destination.entity.Destination;
import com.backend.domain.destination.repository.DestinationRepository;
import com.backend.domain.favoriteDestination.entity.FavoriteDestination;
import com.backend.domain.favoriteDestination.repository.FavoriteDestinationRepository;
import com.backend.domain.member.member.entity.Member;
import com.backend.global.dto.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteDestinationService {

  private final FavoriteDestinationRepository favoriteDestinationRepository;
  private final DestinationRepository destinationRepository;

  public RsData<Boolean> toggleFavorite(Member member, Long destinationId) {
    Destination dest = destinationRepository.findById(destinationId).orElse(null);

    if(dest == null){
      return RsData.fail("F-1", "해당 여행지를 찾을 수 없습니다.");
    }

    FavoriteDestination favoriteDestination = favoriteDestinationRepository.findByMemberAndDestination(member, dest);

    if(favoriteDestination != null) {
      favoriteDestinationRepository.delete(favoriteDestination);
      return RsData.success("즐겨찾기를 해제했습니다.", false);
    }

    FavoriteDestination favorite = FavoriteDestination.builder()
      .member(member)
      .destination(dest)
      .build();

    favoriteDestinationRepository.save(favorite);

    return RsData.success("즐겨찾기에 추가했습니다.", true);
  }

  public List<FavoriteDestination> findByMember(Member member) {
    return favoriteDestinationRepository.findByMember(member);
  }

  public boolean isFavorite(Member member, Long destinationId) {
    return favoriteDestinationRepository.existsByMemberIdAndDestinationId(member.getId(), destinationId);
  }

  public List<Long> findDestinationIdsByMember(Member member) {
    return favoriteDestinationRepository
      .findAllByMemberId(member.getId())
      .stream()
      .map(fd -> fd.getDestination().getId())
      .toList();
  }
}
