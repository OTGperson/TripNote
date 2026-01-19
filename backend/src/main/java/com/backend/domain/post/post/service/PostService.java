package com.backend.domain.post.post.service;

import com.backend.domain.post.fileUpload.service.GenFileService;
import com.backend.domain.member.member.entity.Member;
import com.backend.domain.post.post.dto.PostResponse;
import com.backend.domain.post.post.entity.Post;
import com.backend.domain.post.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
  private final PostRepository postRepository;
  private final GenFileService genFileService;

  public Post createPost(Member member, String title, String content, boolean isPublic) {
    Post post = Post.builder()
      .authorId(member.getId())
      .title(title)
      .content(content)
      .isPublic(isPublic)
      .build();

    return postRepository.save(post);
  }

  public Post createPost(Member member, String title, String content, boolean isPublic, List<MultipartFile> images) {
    Post post = createPost(member, title, content, isPublic);

    if(images != null && !images.isEmpty()) {
      savePostImages(post.getId(), images);
    }

    return post;
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

    List<String> imageUrls = getPostImageUrls(post.getId());

    return PostResponse.from(post, imageUrls);
  }

  public List<Post> getMyPosts(Long authorId) {
    return postRepository.findByAuthorIdOrderByCreatedAtDesc(authorId);
  }

  public List<PostResponse> getPublicPosts() {
    List<Post> posts = postRepository.findByIsPublicTrueOrderByCreatedAtDesc();

    return posts.stream()
      .map(post -> PostResponse.from(post, getPostImageUrls(post.getId())))
      .toList();
  }

  public List<String> getPostImageUrls(Long postId) {
    return genFileService
      .getRelFiles("post", postId, "post", "img")
      .stream()
      .map(genFileService::getUrl)
      .toList();
  }

  @Transactional
  public void savePostImages(Long postId, List<MultipartFile> files) {
    String relTypeCode = "post";
    String typeCode = "post";
    String type2Code = "img";

    genFileService.saveFiles(files, relTypeCode, postId, typeCode, type2Code);
  }
}
