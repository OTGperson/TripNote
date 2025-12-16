package com.backend.domain.post.service;

import com.backend.domain.member.member.entity.Member;
import com.backend.domain.post.dto.PostCreateRequest;
import com.backend.domain.post.dto.PostResponse;
import com.backend.domain.post.entity.Post;
import com.backend.domain.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
  private final PostRepository postRepository;

  public PostResponse createPost(Member member, PostCreateRequest req) {
    Post post = Post.builder()
      .authorId(req.getAuthorId())
      .title(req.getTitle())
      .content(req.getContent())
      .isPublic(req.getIsPublic())
      .build();

    Post saved = postRepository.save(post);
    return PostResponse.from(saved);
  }

  public PostResponse getPost(Long id, Member member) {
    Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

    if(!post.isPublic()) {
      if (member == null) {
        throw new SecurityException("비공개 게시글입니다. 로그인 후 이용해주세요.");
      }
      boolean isAuthor = post.getAuthorId().equals(member.getId());
      boolean isAdmin = member.getRole().name().equals("ADMIN");
      if(!isAuthor && !isAdmin) {
        throw new SecurityException("비공개 게시글입니다. 이 게시글에 접근할 권한이 없습니다.");
      }
    }

    return PostResponse.from(post);
  }

  public List<PostResponse> getMyPosts(Member member) {
    return postRepository.findByAuthorIdOrderByCreatedAtDesc(member.getId())
      .stream()
      .map(PostResponse::from)
      .toList();
  }

  public List<PostResponse> getPublicPosts() {
    return postRepository.findByIsPublicTrueOrderByCreatedAtDesc()
      .stream()
      .map(PostResponse::from)
      .toList();
  }


}
