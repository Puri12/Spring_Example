package com.example.advanced.service;

import com.example.advanced.controller.request.PostRequestDto;
import com.example.advanced.controller.response.CommentResponseDto;
import com.example.advanced.controller.response.PostListResponseDto;
import com.example.advanced.controller.response.PostResponseDto;
import com.example.advanced.controller.response.ResponseDto;
import com.example.advanced.controller.response.SubCommentResponseDto;
import com.example.advanced.domain.Comment;
import com.example.advanced.domain.CommentLike;
import com.example.advanced.domain.Member;
import com.example.advanced.domain.Post;
import com.example.advanced.domain.PostLike;
import com.example.advanced.domain.SubComment;
import com.example.advanced.domain.SubCommentLike;
import com.example.advanced.jwt.TokenProvider;
import com.example.advanced.repository.CommentLikeRepository;
import com.example.advanced.repository.CommentRepository;
import com.example.advanced.repository.PostLikeRepository;
import com.example.advanced.repository.PostRepository;
import com.example.advanced.repository.SubCommentLikeRepository;
import com.example.advanced.repository.SubCommentRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;
  private final PostLikeRepository postLikeRepository;
  private final CommentRepository commentRepository;
  private final CommentLikeRepository commentLikeRepository;
  private final SubCommentRepository subCommentRepository;
  private final SubCommentLikeRepository subCommentLikeRepository;

  private final TokenProvider tokenProvider;

  @Transactional
  public ResponseDto<?> createPost(PostRequestDto requestDto, HttpServletRequest request) {
    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "refresh token is invalid");
    }

    Post post = Post.builder()
        .title(requestDto.getTitle())
        .content(requestDto.getContent())
        .imgUrl(requestDto.getImgUrl())
        .member(member)
        .build();
    postRepository.save(post);
    return ResponseDto.success(
        PostResponseDto.builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .imgUrl(post.getImgUrl())
            .author(post.getMember().getNickname())
            .likes(countLikesPost(post))
            .createdAt(post.getCreatedAt())
            .modifiedAt(post.getModifiedAt())
            .build()
    );
  }

  @Transactional(readOnly = true)
  public ResponseDto<?> getPost(Long id) {
    Post post = isPresentPost(id);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "post id is not exist");
    }

    List<Comment> commentList = commentRepository.findAllByPost(post);
    List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

    for (Comment comment : commentList) {
      List<SubComment> subCommentList = subCommentRepository.findAllByCommentId(comment.getId());
      List<SubCommentResponseDto> subCommentResponseDtoList = new ArrayList<>();
      for (SubComment subComment : subCommentList) {
        subCommentResponseDtoList.add(
            SubCommentResponseDto.builder()
            .id(subComment.getId())
            .content(subComment.getContent())
            .author(subComment.getMember().getNickname())
            .likes(countLikesSubCommentLike(subComment))
            .createdAt(subComment.getCreatedAt())
            .modifiedAt(subComment.getModifiedAt())
            .build()
        );
      }
      commentResponseDtoList.add(
          CommentResponseDto.builder()
              .id(comment.getId())
              .author(comment.getMember().getNickname())
              .content(comment.getContent())
              .likes(countLikesComment(comment))
              .subComments(subCommentResponseDtoList)
              .createdAt(comment.getCreatedAt())
              .modifiedAt(comment.getModifiedAt())
              .build()
      );
    }

    return ResponseDto.success(
        PostResponseDto.builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .author(post.getMember().getNickname())
            .imgUrl(post.getImgUrl())
            .likes(countLikesPost(post))
            .comments(commentResponseDtoList)
            .createdAt(post.getCreatedAt())
            .modifiedAt(post.getModifiedAt())
            .build()
    );
  }

  @Transactional(readOnly = true)
  public ResponseDto<?> getAllPost() {
    List<Post> postList = postRepository.findAllByOrderByModifiedAtDesc();
    List<PostListResponseDto> postListResponseDtoList = new ArrayList<>();
    for (Post post : postList) {
      int comments = commentRepository.countAllByPost(post);
      int postLikes = postLikeRepository.findAllByPost(post).size();
      postListResponseDtoList.add(
          PostListResponseDto.builder()
              .id(post.getId())
              .title(post.getTitle())
              .content(post.getContent())
              .author(post.getMember().getNickname())
              .likes(postLikes)
              .imgUrl(post.getImgUrl())
              .commentsNum(comments)
              .createdAt(post.getCreatedAt())
              .modifiedAt(post.getModifiedAt())
              .build()
      );
    }

    return ResponseDto.success(postListResponseDtoList);
  }

  @Transactional
  public ResponseDto<?> updatePost(Long id, PostRequestDto requestDto, HttpServletRequest request) {
    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "refresh token is invalid");
    }

    Post post = isPresentPost(id);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "post id is not exist");
    }

    if (post.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "only author can update");
    }

    post.update(requestDto);
    return ResponseDto.success(
        PostResponseDto.builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .imgUrl(post.getImgUrl())
            .author(post.getMember().getNickname())
            .likes(countLikesPost(post))
            .createdAt(post.getCreatedAt())
            .modifiedAt(post.getModifiedAt())
            .build()
    );
  }

  @Transactional
  public ResponseDto<?> deletePost(Long id, HttpServletRequest request) {
    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "refresh token is invalid");
    }

    Post post = isPresentPost(id);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "post id is not exist");
    }

    if (post.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "only author can delete");
    }

    postRepository.delete(post);
    return ResponseDto.success("delete success");
  }

  @Transactional(readOnly = true)
  public ResponseDto<?> getAllPostByMember(HttpServletRequest request) {
    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "refresh token is invalid");
    }

    List<Post> postList = postRepository.findAllByMember(member);
    List<PostListResponseDto> postListResponseDtoList = new ArrayList<>();
    for (Post post : postList) {
      int comments = commentRepository.countAllByPost(post);
      int postLikes = postLikeRepository.findAllByPost(post).size();
      postListResponseDtoList.add(
          PostListResponseDto.builder()
              .id(post.getId())
              .title(post.getTitle())
              .content(post.getContent())
              .author(post.getMember().getNickname())
              .imgUrl(post.getImgUrl())
              .likes(postLikes)
              .commentsNum(comments)
              .createdAt(post.getCreatedAt())
              .modifiedAt(post.getModifiedAt())
              .build()
      );
    }

    return ResponseDto.success(postListResponseDtoList);
  }

  @Transactional(readOnly = true)
  public int countLikesPost(Post post) {
    List<PostLike> postLikeList = postLikeRepository.findAllByPost(post);
    return postLikeList.size();
  }

  @Transactional(readOnly = true)
  public int countLikesComment(Comment comment) {
    List<CommentLike> commentLikeList = commentLikeRepository.findAllByComment(comment);
    return commentLikeList.size();
  }

  @Transactional(readOnly = true)
  public int countLikesSubCommentLike(SubComment subComment) {
    List<SubCommentLike> subCommentLikeList = subCommentLikeRepository.findAllBySubComment(subComment);
    return subCommentLikeList.size();
  }

  @Transactional(readOnly = true)
  public Post isPresentPost(Long id) {
    Optional<Post> optionalPost = postRepository.findById(id);
    return optionalPost.orElse(null);
  }

  @Transactional
  public Member validateMember(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
      return null;
    }
    return tokenProvider.getMemberFromAuthentication();
  }

}
