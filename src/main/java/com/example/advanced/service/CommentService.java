package com.example.advanced.service;

import com.example.advanced.controller.request.CommentRequestDto;
import com.example.advanced.controller.response.CommentResponseDto;
import com.example.advanced.controller.response.ResponseDto;
import com.example.advanced.controller.response.SubCommentResponseDto;
import com.example.advanced.domain.Comment;
import com.example.advanced.domain.CommentLike;
import com.example.advanced.domain.Member;
import com.example.advanced.domain.Post;
import com.example.advanced.domain.SubComment;
import com.example.advanced.domain.SubCommentLike;
import com.example.advanced.jwt.TokenProvider;
import com.example.advanced.repository.CommentLikeRepository;
import com.example.advanced.repository.CommentRepository;
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
public class CommentService {

  private final CommentRepository commentRepository;
  private final CommentLikeRepository commentLikeRepository;
  private final SubCommentRepository subCommentRepository;
  private final SubCommentLikeRepository subCommentLikeRepository;

  private final TokenProvider tokenProvider;
  private final PostService postService;

  @Transactional
  public ResponseDto<?> createComment(CommentRequestDto requestDto, HttpServletRequest request) {
    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "refresh token is invalid");
    }

    Post post = postService.isPresentPost(requestDto.getPostId());
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "post id is not exist");
    }

    Comment comment = Comment.builder()
        .member(member)
        .post(post)
        .content(requestDto.getContent())
        .build();
    commentRepository.save(comment);

    return ResponseDto.success(
        CommentResponseDto.builder()
            .id(comment.getId())
            .author(comment.getMember().getNickname())
            .content(comment.getContent())
            .createdAt(comment.getCreatedAt())
            .modifiedAt(comment.getModifiedAt())
            .build()
    );
  }

  @Transactional(readOnly = true)
  public ResponseDto<?> getAllCommentsByPost(Long postId) {
    Post post = postService.isPresentPost(postId);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "post id is not exist");
    }

    List<Comment> commentList = commentRepository.findAllByPost(post);
    List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

    for (Comment comment : commentList) {
      commentResponseDtoList.add(
          CommentResponseDto.builder()
              .id(comment.getId())
              .author(comment.getMember().getNickname())
              .content(comment.getContent())
              .likes(countLikesComment(comment))
              .createdAt(comment.getCreatedAt())
              .modifiedAt(comment.getModifiedAt())
              .build()
      );
    }
    return ResponseDto.success(commentResponseDtoList);
  }

  @Transactional(readOnly = true)
  public ResponseDto<?> getAllCommentsByMember(HttpServletRequest request) {
    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "refresh token is invalid");
    }

    List<Comment> commentList = commentRepository.findAllByMember(member);
    List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

    for (Comment comment : commentList) {
      commentResponseDtoList.add(
          CommentResponseDto.builder()
              .id(comment.getId())
              .author(comment.getMember().getNickname())
              .content(comment.getContent())
              .likes(countLikesComment(comment))
              .createdAt(comment.getCreatedAt())
              .modifiedAt(comment.getModifiedAt())
              .build()
      );
    }
    return ResponseDto.success(commentResponseDtoList);
  }

  @Transactional
  public ResponseDto<?> updateComment(Long id, CommentRequestDto requestDto, HttpServletRequest request) {
    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "refresh token is invalid");
    }

    Post post = postService.isPresentPost(requestDto.getPostId());
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "post id is not exist");
    }

    Comment comment = isPresentComment(id);
    if (null == comment) {
      return ResponseDto.fail("NOT_FOUND", "comment id is not exist");
    }

    if (comment.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "only author can update");
    }

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

    comment.update(requestDto);
    return ResponseDto.success(
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

  @Transactional
  public ResponseDto<?> deleteComment(Long id, HttpServletRequest request) {
    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "refresh token is invalid");
    }

    Comment comment = isPresentComment(id);
    if (null == comment) {
      return ResponseDto.fail("NOT_FOUND", "comment id is not exist");
    }

    if (comment.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "only author can update");
    }

    List<SubComment> subCommentList = subCommentRepository.findAllByCommentId(comment.getId());
    for (SubComment subComment : subCommentList) {
      subCommentRepository.delete(subComment);
    }

    commentRepository.delete(comment);
    return ResponseDto.success("success");
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
  public Comment isPresentComment(Long id) {
    Optional<Comment> optionalComment = commentRepository.findById(id);
    return optionalComment.orElse(null);
  }

  @Transactional
  public Member validateMember(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
      return null;
    }
    return tokenProvider.getMemberFromAuthentication();
  }
}
