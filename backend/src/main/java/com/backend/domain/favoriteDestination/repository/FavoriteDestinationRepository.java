package com.backend.domain.favoriteDestination.repository;

import com.backend.domain.destination.entity.Destination;
import com.backend.domain.favoriteDestination.entity.FavoriteDestination;
import com.backend.domain.member.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteDestinationRepository extends JpaRepository<FavoriteDestination, Long> {
  FavoriteDestination findByMemberAndDestination(Member member, Destination destination);

  List<FavoriteDestination> findByMember(Member member);

  boolean existsByMemberIdAndDestinationId(Long id, Long destinationId);

  List<FavoriteDestination> findAllByMemberId(Long memberId);
}
