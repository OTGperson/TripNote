package com.backend.domain.post.controller;

import com.backend.domain.member.member.entity.Member;
import com.backend.domain.post.dto.PostCreateRequest;
import com.backend.domain.post.dto.PostResponse;
import com.backend.domain.post.service.PostService;
import com.backend.global.dto.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
public class PostController {
  private final PostService postService;

  @PostMapping
  public RsData<PostResponse> createPost(@AuthenticationPrincipal Member member, @RequestBody PostCreateRequest req) {
    if(member == null) {
      return RsData.fail("F-1", "로그인 후 이용해 주세요.");
    }

    if(req.getTitle() == null || req.getTitle().trim().isEmpty()) {
      return RsData.fail("F-1", "제목을 입력해주세요.");
    }

    if(req.getContent() == null || req.getContent().trim().isEmpty()) {
      return RsData.fail("F-1", "제목을 입력해주세요.");
    }

    PostResponse res = postService.createPost(member, req);

    return RsData.success("게시글이 작성되었습니다.", res);
  }

  @GetMapping("/{id}")
  public RsData<PostResponse> getPost(@AuthenticationPrincipal Member member, @PathVariable Long id) {
    PostResponse post = postService.getPost(id, member);

    return RsData.success("게시글을 조회했습니다.", post);
  }

  @GetMapping("/me")
  public RsData<List<PostResponse>> getMyPosts(@AuthenticationPrincipal Member member) {
    if(member == null) {
      return RsData.fail("F-1", "로그인 후 이용해 주세요.");
    }

    List<PostResponse> posts = postService.getMyPosts(member);
    return RsData.success("나의 게시글 목록을 조회했습니다.", posts);
  }

  @GetMapping("/public")
  public RsData<List<PostResponse>> getPublicPosts() {
    List<PostResponse> posts = postService.getPublicPosts();
    return RsData.success("전체 공개 게시글 목록을 조회했습니다.", posts);
  }
}
